/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.sensing;

import android.graphics.PointF;
import android.text.method.Touch;
import android.util.Log;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.utils.TouchUtil;

import java.util.ArrayList;

public final class CollisionDetection {

	private CollisionDetection() {
	}

	public static double checkCollisionBetweenLooks(Look firstLook, Look secondLook) {

		if (!firstLook.isVisible() || !secondLook.isVisible()) {
			return 0d;
		}

		if (!firstLook.getHitbox().overlaps(secondLook.getHitbox())) {
			return 0d;
		}

		boolean colliding = checkCollisionBetweenPolygons(firstLook.getCurrentCollisionPolygon(),
				secondLook.getCurrentCollisionPolygon());

		return colliding ? 1d : 0d;
	}

	public static boolean checkCollisionBetweenPolygons(Polygon[] first, Polygon[] second) {
		Rectangle[] firstBoxes = createBoundingBoxesOfCollisionPolygons(first);
		Rectangle[] secondBoxes = createBoundingBoxesOfCollisionPolygons(second);

		for (int firstIndex = 0; firstIndex < first.length; firstIndex++) {
			for (int secondIndex = 0; secondIndex < second.length; secondIndex++) {
				if (firstBoxes[firstIndex].overlaps(secondBoxes[secondIndex])
						&& intersectPolygons(first[firstIndex], second[secondIndex])) {
					return true;
				}
			}
		}
		return checkCollisionForPolygonsInPolygons(first, second);
	}

	private static Rectangle[] createBoundingBoxesOfCollisionPolygons(Polygon[] polygons) {
		Rectangle[] boundingBoxes = new Rectangle[polygons.length];
		for (int i = 0; i < polygons.length; i++) {
			boundingBoxes[i] = polygons[i].getBoundingRectangle();
		}
		return boundingBoxes;
	}

	public static boolean intersectPolygons(Polygon first, Polygon second) {
		float[] firstVertices = first.getTransformedVertices();
		int firstLength = firstVertices.length;
		Vector2 v1 = new Vector2();
		Vector2 v2 = new Vector2();

		for (int firstIndex = 0; firstIndex < firstLength; firstIndex += 2) {
			v1.x = firstVertices[firstIndex];
			v1.y = firstVertices[firstIndex + 1];
			v2.x = firstVertices[(firstIndex + 2) % firstLength];
			v2.y = firstVertices[(firstIndex + 3) % firstLength];

			if (Intersector.intersectSegmentPolygon(v1, v2, second)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkCollisionForPolygonsInPolygons(Polygon[] first, Polygon[] second) {
		for (Polygon firstPolygon : first) {
			int containedIn = 0;
			for (Polygon secondPolygon : second) {
				if (secondPolygon.contains(firstPolygon.getTransformedVertices()[0], firstPolygon.getTransformedVertices()[1])) {
					containedIn++;
				}
			}
			if (containedIn % 2 != 0) {
				return true;
			}
		}

		for (Polygon secondPolygon : second) {
			int containedIn = 0;
			for (Polygon firstPolygon : first) {
				if (firstPolygon.contains(secondPolygon.getTransformedVertices()[0], secondPolygon.getTransformedVertices()[1])) {
					containedIn++;
				}
			}
			if (containedIn % 2 != 0) {
				return true;
			}
		}
		return false;
	}

	public static double collidesWithEdge(Look look) {
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().getXmlHeader().virtualScreenHeight;

		Rectangle screen = new Rectangle(-virtualScreenWidth/2, -virtualScreenHeight/2, virtualScreenWidth,
				virtualScreenHeight);

		for (Polygon polygon : look.getCurrentCollisionPolygon()) {
			for (int i = 0; i < polygon.getTransformedVertices().length - 4; i += 2) {
				Vector2 firstPoint = new Vector2(polygon.getTransformedVertices()[i],
						polygon.getTransformedVertices()[i + 1]);
				Vector2 secondPoint = new Vector2(polygon.getTransformedVertices()[i + 2],
						polygon.getTransformedVertices()[i + 3]);
				if (screen.contains(firstPoint) ^ screen.contains(secondPoint)) return 1.0d;
			}
		}
		return 0d;
	}

	public static double collidesWithFinger(Look look) {
		ArrayList<PointF> touchingPoints = TouchUtil.getCurrentTouchingPoints();
		Vector2 start = new Vector2();
		Vector2 end = new Vector2();
		Vector2 center = new Vector2();
		float touchRadius = Constants.COLLISION_WITH_FINGER_TOUCH_RADIUS;

		for (PointF point : touchingPoints) {
			center.set(point.x, point.y);
			for (Polygon polygon : look.getCurrentCollisionPolygon()) {
				Rectangle boundingRectangle = polygon.getBoundingRectangle();
				boundingRectangle.x -= touchRadius;
				boundingRectangle.y -= touchRadius;
				boundingRectangle.width += touchRadius*2;
				boundingRectangle.height += touchRadius*2;

				if (polygon.contains(point.x, point.y)) {
					return 1d;
				}

				float[] vertices = polygon.getTransformedVertices();
				int f = 0;
				while (f < polygon.getVertices().length - 3) {
					start.x = vertices[f++];
					start.y = vertices[f++];
					end.x = vertices[f++];
					end.y = vertices[f++];
					if (Intersector.intersectSegmentCircle(start, end, center, touchRadius*touchRadius)) {
						return 1d;
					}
				}
			}
		}
		return 0d;
	}
}
