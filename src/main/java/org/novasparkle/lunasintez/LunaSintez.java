package org.novasparkle.lunasintez;

import lombok.Getter;
import org.novasparkle.lunasintez.configuration.ConfigManager;
import org.novasparkle.lunasintez.listener.SpawnerEvent;
import org.novasparkle.lunaspring.API.commands.CommandInitializer;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satespawnerapi.SateSpawnerAPI;
import org.satellite.dev.progiple.satespawnerapi.api.APIComponent;
public final class LunaSintez extends LunaPlugin {
    @Getter
    private static LunaSintez instance;
    @Getter
    private APIComponent lunaSintezComponent;
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        this.loadFiles(
                "menus/EvoMenu.yml",
                "menus/MainMenu.yml",
                "menus/EvoMainMenu.yml",
                "menus/SintezMenu.yml",
                "menus/InstructionMenu.yml"
        );
        CommandInitializer.initialize(this, "#.commands");
        this.lunaSintezComponent = new APIComponent(this.getName(), ConfigManager.getInt("SSAPI.priority"));
        SateSpawnerAPI.getInstance().registerApi(this.lunaSintezComponent);
        this.registerListeners(new SpawnerEvent());
        super.onEnable();
    }
}
