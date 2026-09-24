package wbs.utils.util.particles;

import wbs.utils.util.providers.NumProvider;

public interface SpeedParticleEffect {
    double getSpeed();

    NumProvider getSpeedProvider();

    SpeedParticleEffect setSpeed(double speed);

    SpeedParticleEffect setSpeed(NumProvider speed);
}
