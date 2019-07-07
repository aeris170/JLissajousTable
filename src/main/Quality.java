package main;

public enum Quality {
	FASTEST, BALANCED, BEST {
		@Override
		public Quality next() {
			return FASTEST;
		}
	};

	public Quality next() {
		return values()[ordinal() + 1];
	}
}
