package com.viintro.Viintro.widgets;

public class Configuration {



  public static final int DURATION_INFINITE = -1;

  public static final int DURATION_SHORT = 3000;

  public static final int DURATION_LONG = 5000;


  public static final Configuration DEFAULT;

  static {
    DEFAULT = new Builder().setDuration(DURATION_SHORT).build();
  }


  final int durationInMilliseconds;
  /** The resource id for the in animation. */
  final int inAnimationResId;
  /** The resource id for the out animation. */
  final int outAnimationResId;

  private Configuration(Builder builder) {
    this.durationInMilliseconds = builder.durationInMilliseconds;
    this.inAnimationResId = builder.inAnimationResId;
    this.outAnimationResId = builder.outAnimationResId;
  }

  /** Creates a {@link Builder} to build a {@link Configuration} upon. */
  public static class Builder {
    private int durationInMilliseconds = DURATION_SHORT;
    private int inAnimationResId = 0;
    private int outAnimationResId = 0;


    public Builder setDuration(final int duration) {
      this.durationInMilliseconds = duration;

      return this;
    }


    public Builder setInAnimation(final int inAnimationResId) {
      this.inAnimationResId = inAnimationResId;

      return this;
    }


     public Builder setOutAnimation(final int outAnimationResId) {
      this.outAnimationResId = outAnimationResId;

      return this;
    }

    /**
     * Builds the {@link Configuration}.
     *
     * @return The built {@link Configuration}.
     */
    public Configuration build() {
      return new Configuration(this);
    }
  }

  @Override
  public String toString() {
    return "Configuration{" +
      "durationInMilliseconds=" + durationInMilliseconds +
      ", inAnimationResId=" + inAnimationResId +
      ", outAnimationResId=" + outAnimationResId +
      '}';
  }
}