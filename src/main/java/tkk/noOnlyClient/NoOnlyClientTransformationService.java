package tkk.noOnlyClient;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NoOnlyClientTransformationService implements ITransformationService {
    static {
        try {
            Field field= Launcher.class.getDeclaredField("launchPlugins");
            field.setAccessible(true);
            Object obj=field.get(Launcher.INSTANCE);
            field= LaunchPluginHandler.class.getDeclaredField("plugins");
            field.setAccessible(true);
            ((Map<String, ILaunchPluginService>)field.get(obj)).put("tkknoonlyclient",new NoOnlyClientLaunchPluginService());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    @Nonnull
    @Override
    public String name() {
        return "tkknoonlyclientTransformationService";
    }

    @Override
    public void initialize(IEnvironment environment) {

    }


    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    @Nonnull
    @Override
    public @NotNull List<ITransformer> transformers() {
        return Collections.emptyList();
    }
}
