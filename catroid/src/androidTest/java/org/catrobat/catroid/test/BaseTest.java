/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.test;

import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;

import com.badlogic.gdx.physics.box2d.Body;

import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsProperties;
import org.catrobat.catroid.physics.PhysicsWorld;

import static org.catrobat.catroid.physics.PhysicsProperties.*;

/**
 * Created by michaelfischl on 05.03.17.
 */

public class BaseTest extends AndroidTestCase {

	PhysicsWorld physicsWorld;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		physicsWorld = new PhysicsWorld();
	}

	public Sprite createSprite(String name) {
		Sprite sprite = new SingleSprite(name);
		Body body = physicsWorld.createBody();
		PhysicsProperties physicsProperties = new PhysicsProperties(body, sprite);
		sprite.setPhysicsProperties(physicsProperties);
		sprite.getPhysicsProperties().setType(Type.NONE);
		return sprite;
	}

	public Sprite createSprite(String name, Type type) {
		Sprite sprite = new SingleSprite(name);
		Body body = physicsWorld.createBody();
		PhysicsProperties physicsProperties = new PhysicsProperties(body, sprite);
		sprite.setPhysicsProperties(physicsProperties);
		sprite.getPhysicsProperties().setType(type);
		return sprite;
	}
}