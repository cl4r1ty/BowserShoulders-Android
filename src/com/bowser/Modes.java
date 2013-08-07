package com.bowser;

public enum Modes {
	
TEST, ONECOLOR, BACKANDFORHT, INANDOUT, SHIFTY,
RANDOM, UPANDDOWN, RAINBOWCYCLE, RAINBOW, MIC;


	public static Modes getEnum(final int enumOrdinal)
	{
		return (enumOrdinal >= Modes.values().length) ? null : Modes.values()[enumOrdinal];
	}
}