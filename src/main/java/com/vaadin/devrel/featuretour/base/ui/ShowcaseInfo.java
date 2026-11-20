package com.vaadin.devrel.featuretour.base.ui;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Card text and tier badges of a showcase page. The overview pages read it
 * through the {@code menuClass} of each menu entry, so every page describes
 * itself in one place.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ShowcaseInfo {

    /** One sentence for the feature card. */
    String summary();

    /** License and stability badges. */
    Tier[] tiers() default {};
}
