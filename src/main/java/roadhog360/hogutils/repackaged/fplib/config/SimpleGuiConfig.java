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

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class SimpleGuiConfig extends GuiConfig {

    public SimpleGuiConfig(GuiScreen parent, Class<?> configClass, String modID, String modName)
            throws ConfigException {
        this(parent, modID, modName, false, configClass);
    }

    public SimpleGuiConfig(GuiScreen parent, String modID, String modName, Class<?>... configClasses)
            throws ConfigException {
        this(parent, modID, modName, false, configClasses);
    }

    public SimpleGuiConfig(GuiScreen parent, String modID, String modName) throws ConfigException {
        this(parent, modID, modName, true, ConfigurationManager.getConfigClasses(modID));
    }

    public SimpleGuiConfig(GuiScreen parent, String modID, String modName, boolean categorized,
            Class<?>... configClasses) throws ConfigException {
        super(
                parent,
                ConfigurationManager.getConfigElementsMulti(categorized, configClasses),
                modID,
                false,
                false,
                modName + " Configuration");
    }
}
