package tkk.noOnlyClient;

import com.google.common.collect.Streams;
import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.OnlyIns;
import net.minecraftforge.fml.loading.RuntimeDistCleaner;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoOnlyClientLaunchPluginService  implements ILaunchPluginService
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Marker DISTXFORM = MarkerFactory.getMarker("TKKDISTXFORM");
    private static String DIST=Dist.DEDICATED_SERVER.name();
    private static final String ONLYIN = Type.getDescriptor(OnlyIn.class);
    private static final String ONLYINS = Type.getDescriptor(OnlyIns.class);
    @Override
    public String name()
    {
        return "tkknoonlyclient";
    }

    @Override
    public int processClassWithFlags(final Phase phase, final ClassNode classNode, final Type classType, final String reason)
    {
        List<AnnotationNode> unpack=unpack(classNode.visibleAnnotations).stream().
                filter(ann->Objects.equals(ann.desc, ONLYIN)).
                filter(ann->ann.values.indexOf("_interface") == -1).
                filter(ann -> !Objects.equals(((String[])ann.values.get(ann.values.indexOf("value")+1))[1], DIST)).collect(Collectors.toList());

        //NoOnlyClientMod.LOGGER.log(Level.ERROR,"tkk processClassWithFlags "+classNode.name);
        if(!unpack.isEmpty()){
            classNode.visibleAnnotations.removeAll(unpack);
            LOGGER.error(DISTXFORM, "tkk remove dist {}", classNode.name);
            return ComputeFlags.COMPUTE_FRAMES;
        }
        //return changes.get() ? ComputeFlags.COMPUTE_FRAMES : ComputeFlags.NO_REWRITE;

        return ComputeFlags.NO_REWRITE;
    }

    @SuppressWarnings("unchecked")
    private static List<AnnotationNode> unpack(final List<AnnotationNode> anns) {
        if (anns == null) return Collections.emptyList();
        List<AnnotationNode> ret = anns.stream().filter(ann->Objects.equals(ann.desc, ONLYIN)).collect(Collectors.toList());
        anns.stream().filter(ann->Objects.equals(ann.desc, ONLYINS) && ann.values != null)
                .map( ann -> (List<AnnotationNode>)ann.values.get(ann.values.indexOf("value") + 1))
                .filter(v -> v != null)
                .forEach(v -> v.forEach(ret::add));
        return ret;
    }

    private boolean remove(final List<AnnotationNode> anns, final String side)
    {
        return unpack(anns).stream().
                filter(ann->Objects.equals(ann.desc, ONLYIN)).
                filter(ann->ann.values.indexOf("_interface") == -1).
                anyMatch(ann -> !Objects.equals(((String[])ann.values.get(ann.values.indexOf("value")+1))[1], side));
    }

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.BEFORE);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty)
    {
        return isEmpty ? NAY : YAY;
    }

}
