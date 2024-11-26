/*
 * This file is part of FalsePatternLib, modified by GTNHLib and then HogUtils.
 *
 * Copyright (C) 2022-2024 FalsePattern
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * FalsePatternLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FalsePatternLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FalsePatternLib. If not, see <https://www.gnu.org/licenses/>.
 */

package roadhog360.hogutils.repackaged.fplib.config;

import org.spongepowered.asm.mixin.MixinEnvironment;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {

    /**
     * The mod id that this configuration is associated with.
     */
    String modid();

    /**
     * Root element category, defaults to "general".
     */
    String category() default "general";

    /**
     * The subdirectory of the config directory to use. Defaults to none (config/). If you want to use a subdirectory,
     * you must specify it as a relative path (e.g. "myMod").
     */
    String configSubDirectory() default "";

    /**
     * The name of the configuration file. Defaults to the modid. The file extension (.cfg) is added automatically.
     */
    String filename() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.TYPE })
    @interface LangKey {

        String value();
    }

    /**
     * Defines a pattern for generating lang keys for fields and categories in the annotated class.
     * <p>
     * Placeholders: <br>
     * {@code %mod} - mod id <br>
     * {@code %file} - file name <br>
     * {@code %cat} - category name <br>
     * {@code %field} - field name <b>(required)</b> <br>
     * </p>
     * Default pattern: {@code %mod.%cat.%field}. Categories use the pattern without {@code %field}. Can be overridden
     * for fields with {@link Config.LangKey}. <br>
     * The generated keys can be printed to log by setting the {@code -Dgtnhlib.printkeys=true} JVM flag or dumped to a
     * file in the base minecraft directory by setting the {@code -Dgtnhlib.dumpkeys=true} JVM flag.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface LangKeyPattern {

        String pattern() default "%mod.%cat.%field";

        /**
         * Whether subcategories should use their fully qualified name.<br>
         * Fully qualified: {@code category.category1.category2} <br>
         * Normal: {@code category2}
         */
        boolean fullyQualified() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.TYPE })
    @interface Comment {

        String[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Ignore {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DefaultBoolean {

        boolean value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface RangeInt {

        int min() default Integer.MIN_VALUE;

        int max() default Integer.MAX_VALUE;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DefaultInt {

        int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DefaultIntList {

        int[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface RangeFloat {

        float min() default -Float.MAX_VALUE;

        float max() default Float.MAX_VALUE;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DefaultFloat {

        float value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface RangeDouble {

        double min() default -Double.MAX_VALUE;

        double max() default Double.MAX_VALUE;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DefaultDouble {

        double value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DefaultDoubleList {

        double[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DefaultString {

        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Pattern {

        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DefaultEnum {

        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface DefaultStringList {

        String[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Name {

        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.TYPE })
    @interface RequiresMcRestart {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.TYPE })
    @interface RequiresWorldRestart {}

    /**
     * Set a default value if the listed coremod or modID is found. Coremod will take precedence value will be parsed to
     * target field's type
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface ModDetectedDefault {

        String coremod() default "";

        String modID() default "";

        String value() default "";

        /**
         * Can be used instead of value() for array fields
         */
        String[] values() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface ModDetectedDefaultList {

        ModDetectedDefault[] values() default {};
    }

    /**
     * Excludes this class from the auto config GUI, only applicable to a {@link Config} annotated class. Has no effect
     * if a gui factory is registered for the mod.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface ExcludeFromAutoGui {}

    /// Should this config field only appear on one side?
    /// Takes {@link org.spongepowered.asm.mixin.MixinEnvironment.Side}, NOT {@link cpw.mods.fml.relauncher.Side}
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface OnlyOn {
        MixinEnvironment.Side value();
    }
}
