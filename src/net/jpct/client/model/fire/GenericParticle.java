package net.jpct.client.model.fire;
import java.awt.Color;

import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.Light;

/**
 * A generic particle handler.
 *
 */
public class GenericParticle {

	private final SimpleVector origin;
	private final Particle[] particles;
	private final int PARTICLE_COUNT = 1;
	private int count = 0;
	private long delay = System.currentTimeMillis();
	private int cycle = 0;
	private boolean firstTick = false;

	public GenericParticle(final World world, final SimpleVector origin) {
		this.origin = origin;
		this.particles = new Particle[25];
	}
	
	public void update(final World world) {
		if (!firstTick) {
			firstTick = true;
			Light light = new Light(world);
			light.setPosition(new SimpleVector(origin.x, origin.y - 40, origin.z));
			light.setAttenuation(-1);
			light.setIntensity(255, 255, 255);
			light.setDiscardDistance(500);
			light.enable();
		}
		if (System.currentTimeMillis() - delay > 120) {
			cycle++;
			if (cycle % 2 == 0) {
				for (int i = 0; i < PARTICLE_COUNT; i++) {
					// add particle
					Particle p = getParticle(world);
					p.setTranslationMatrix(new Matrix());
					p.setOrigin(origin);
					p.setVelocity(new SimpleVector(1 - Math.random() * 1,  -1.4 - (Math.random() / 2f), 1 - Math.random() * 1));
					p.reset();

				}
				// move the visible particles
				for (int i = 0; i < count; i++) {
					Particle pp = particles[i];
					if (pp.getVisibility()) {
						pp.move(1);
					}
				}
			}
			delay = System.currentTimeMillis();
		}

	}
	
	private Particle getParticle(World w) {
		for (int i = 0; i < count; i++) {
			Particle pp = particles[i];
			if (!pp.getVisibility()) {
				pp.setVisibility(Object3D.OBJ_VISIBLE);
				return pp;
			}
		}
		Particle p = new Particle();
		w.addObject(p);
		particles[count] = p;
		count++;
		return p;
	}

}

@SuppressWarnings("serial")
class Particle extends Object3D {

	private SimpleVector vel = new SimpleVector();
	private long time = 0;
	private long maxTime = 2000;
	private static final SimpleVector GRAV = new SimpleVector(0, -0.50f, 0);

	Particle() {
		super(Primitives.getPlane(2, 5));
		this.setBillboarding(true);
		this.setVisibility(true);
		this.setTransparency(12);
		this.setAdditionalColor(Color.WHITE);
		this.setLighting(Object3D.LIGHTING_NO_LIGHTS);
		this.enableLazyTransformations();
		this.build();
		reset();
	}

	void setVelocity(SimpleVector vel) {
		this.vel.set(vel);
	}

	void reset() {
		this.setTexture("flame");
		time = System.currentTimeMillis();
		getTranslationMatrix().setIdentity();
	}

	void move(int ticks) {
		if (getVisibility()) {
			if (System.currentTimeMillis() - time > 333) {
				this.setTexture("smoke");
			}
			for (int i = 0; i < ticks; i++) {
				vel.add(GRAV);
				this.translate(vel);
			}
			if (System.currentTimeMillis() - time > maxTime) {
				reset();
				this.setVisibility(Object3D.OBJ_INVISIBLE);
			}
		}
	}
	
}