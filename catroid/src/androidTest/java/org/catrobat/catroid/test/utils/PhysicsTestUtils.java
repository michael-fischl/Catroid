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
package org.catrobat.catroid.test.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.physics.PhysicsProperties;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.utils.Utils;

import java.io.File;

public final class PhysicsTestUtils {

	private PhysicsTestUtils() {
		throw new AssertionError();
	}

	public static PhysicsProperties createPhysicsObject(PhysicsWorld physicsWorld, PhysicsProperties.Type type, float width,
			float height) {
		return createPhysicsObject(physicsWorld, type, createRectanglePolygonShape(width, height));
	}

	public static PolygonShape createRectanglePolygonShape(float width, float height) {
		PolygonShape rectangle = new PolygonShape();
		rectangle.setAsBox(width / 2.0f, height / 2.0f);
		return rectangle;
	}

	public static PhysicsProperties createPhysicsObject(PhysicsWorld physicsWorld, PhysicsProperties.Type type, Shape shape) {
		PhysicsProperties physicsProperties = physicsWorld.getPhysicsObject(new SingleSprite("TestSprite"));

		if (type != null) {
			physicsProperties.setType(type);
		}

		if (shape != null) {
			physicsProperties.setShape(new Shape[] { shape });
		}
		return physicsProperties;
	}

	public static PhysicsProperties createPhysicsObject(PhysicsWorld physicsWorld, PhysicsProperties.Type type) {
		return createPhysicsObject(physicsWorld, type, null);
	}

	public static PhysicsProperties createPhysicsObject(PhysicsWorld physicsWorld) {
		return createPhysicsObject(physicsWorld, null, null);
	}

	public static Body getBody(PhysicsProperties physicsProperties) {
		return (Body) Reflection.getPrivateField(physicsProperties, "body");
	}

	public static PhysicsProperties.Type getType(PhysicsProperties physicsProperties) {
		return (PhysicsProperties.Type) Reflection.getPrivateField(physicsProperties, "type");
	}

	public static float getMass(PhysicsProperties physicsProperties) {
		return (Float) Reflection.getPrivateField(physicsProperties, "mass");
	}

	public static Shape[] getShapes(PhysicsProperties physicsProperties) {
		return (Shape[]) Reflection.getPrivateField(physicsProperties, "shapes");
	}

	public static FixtureDef getFixtureDef(PhysicsProperties physicsProperties) {
		return (FixtureDef) Reflection.getPrivateField(physicsProperties, "fixtureDef");
	}

	public static String getInternalImageFilenameFromFilename(String filename) {
		return Utils.md5Checksum(filename) + "_" + filename;
	}

	public static LookData generateLookData(File testImage) {
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName(testImage.getName());
		Pixmap pixmap = Utils.getPixmapFromFile(testImage);
		lookData.setPixmap(pixmap);
		return lookData;
	}
}
