package gay.tami.imguimc;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ImGuiMC.MOD_ID)
public class ImGuiMC
{
    public static final String MOD_ID = "imguimc";
    static boolean s_displayTestingWindow = true;
    static final KeyMapping s_freeCursorKey = new KeyMapping(
            "key.imguimc.free_cursor",
            KeyConflictContext.UNIVERSAL,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F4,
            "key.category.imguimc"
    );
    private static ImGuiLayer s_imGuiLayer = null;

    public static void disableTestingWindow() {
        s_displayTestingWindow = false;
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(s_freeCursorKey);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if(s_freeCursorKey.consumeClick())
                Minecraft.getInstance().setScreen(new FreeCursorScreen());
        }

        @SubscribeEvent
        public static void onRenderTick(TickEvent.RenderTickEvent event) {
            if(s_imGuiLayer == null) {
                s_imGuiLayer = new ImGuiLayer();
                MinecraftForge.EVENT_BUS.register(s_imGuiLayer);
            }

            if(event.phase == TickEvent.RenderTickEvent.Phase.END)
                s_imGuiLayer.render();
        }
    }
}
