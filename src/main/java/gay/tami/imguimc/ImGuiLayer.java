package gay.tami.imguimc;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.gl3.ImGuiImplGl3;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ScreenEvent;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SUPER;

public class ImGuiLayer {
    private ImGuiImplGl3 _imGuiRenderer = new ImGuiImplGl3();

    private String _inputCharacters = "";
    private float _scroll = 0.0f;

    public ImGuiLayer() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();

        io.addBackendFlags(ImGuiBackendFlags.HasSetMousePos);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);

        io.setIniFilename(null);
        io.setBackendPlatformName("ImGuiMC");

        int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
        keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
        keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
        keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
        keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
        keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
        keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
        keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
        keyMap[ImGuiKey.End] = GLFW_KEY_END;
        keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
        keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
        keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
        keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
        keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
        keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
        keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
        keyMap[ImGuiKey.A] = GLFW_KEY_A;
        keyMap[ImGuiKey.C] = GLFW_KEY_C;
        keyMap[ImGuiKey.V] = GLFW_KEY_V;
        keyMap[ImGuiKey.X] = GLFW_KEY_X;
        keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
        keyMap[ImGuiKey.Z] = GLFW_KEY_Z;
        io.setKeyMap(keyMap);

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(Minecraft.getInstance().getWindow().getWindow(), s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = glfwGetClipboardString(Minecraft.getInstance().getWindow().getWindow());
                if (clipboardString != null) {
                    return clipboardString;
                } else {
                    return "";
                }
            }
        });

        _imGuiRenderer.init();
        //newFrame();
    }

    private void newFrame() {
        ImGuiIO io = ImGui.getIO();

        RenderTarget target = Minecraft.getInstance().getMainRenderTarget();
        io.setDisplaySize(target.viewWidth, target.viewHeight);
        io.setDisplayFramebufferScale((float) target.width / target.viewWidth, (float) target.viewHeight / target.viewHeight);
        io.setDeltaTime(Math.max(Minecraft.getInstance().getDeltaFrameTime() / 20.0f, 0.001f));

        if(Minecraft.getInstance().screen != null) {
            io.addInputCharactersUTF8(_inputCharacters);
            io.setMouseWheel(_scroll);

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

            io.setMouseDelta((float) Minecraft.getInstance().mouseHandler.getXVelocity(), (float) Minecraft.getInstance().mouseHandler.getYVelocity());
            io.setMousePos((float) Minecraft.getInstance().mouseHandler.xpos(), (float) Minecraft.getInstance().mouseHandler.ypos());
        }

        _scroll = 0.0f;
        _inputCharacters = "";
        ImGui.newFrame();
    }

    public void render() {
        newFrame();

        // Testing window
        if(ImGuiMC.s_displayTestingWindow) {
            ImGui.begin("ImGuiMC");

            ImGui.text("This is a testing window to show that ImGuiMC is working!\nTo get rid of this window, call ImGuiMC.disableTestingWindow()");
            if(ImGui.button("Close")) ImGuiMC.disableTestingWindow();

            ImGui.end();
        }

        ImGui.showDemoWindow();

        ImGui.render();
        _imGuiRenderer.renderDrawData(ImGui.getDrawData());
    }

    @SubscribeEvent
    public void onScreenMousePress(ScreenEvent.MouseButtonPressed.Pre event) {
        ImGuiIO io = ImGui.getIO();

        io.setMouseDown(event.getButton(), true);

        if (!io.getWantCaptureMouse() && event.getButton() == 1)
            ImGui.setWindowFocus(null);

        if(io.getWantCaptureMouse())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onScreenMouseRelease(ScreenEvent.MouseButtonReleased.Pre event) {
        ImGuiIO io = ImGui.getIO();

        io.setMouseDown(event.getButton(), false);

        if(io.getWantCaptureMouse())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onScreenMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        ImGuiIO io = ImGui.getIO();

        _scroll = (float) event.getScrollDelta();

        if(io.getWantCaptureMouse())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onScreenKeyPress(ScreenEvent.KeyPressed.Pre event) {
        if(event.getKeyCode() >= 512) return;

        ImGuiIO io = ImGui.getIO();

        io.setKeysDown(event.getKeyCode(), true);
        System.out.println(event.getKeyCode() + " press");

        if(io.getWantTextInput() || (io.getWantCaptureKeyboard() && (
                event.getKeyCode() == GLFW_KEY_TAB ||
                event.getKeyCode() == GLFW_KEY_LEFT ||
                event.getKeyCode() == GLFW_KEY_RIGHT ||
                event.getKeyCode() == GLFW_KEY_UP ||
                event.getKeyCode() == GLFW_KEY_DOWN ||
                event.getKeyCode() == GLFW_KEY_PAGE_UP ||
                event.getKeyCode() == GLFW_KEY_PAGE_DOWN ||
                event.getKeyCode() == GLFW_KEY_HOME ||
                event.getKeyCode() == GLFW_KEY_END ||
                event.getKeyCode() == GLFW_KEY_INSERT ||
                event.getKeyCode() == GLFW_KEY_DELETE ||
                event.getKeyCode() == GLFW_KEY_BACKSPACE ||
                event.getKeyCode() == GLFW_KEY_SPACE ||
                event.getKeyCode() == GLFW_KEY_ENTER ||
                event.getKeyCode() == GLFW_KEY_KP_ENTER ||
                event.getKeyCode() == GLFW_KEY_A ||
                event.getKeyCode() == GLFW_KEY_C ||
                event.getKeyCode() == GLFW_KEY_V ||
                event.getKeyCode() == GLFW_KEY_X ||
                event.getKeyCode() == GLFW_KEY_Y ||
                event.getKeyCode() == GLFW_KEY_Z))
        ) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onScreenKeyRelease(ScreenEvent.KeyReleased.Pre event) {
        if(event.getKeyCode() >= 512) return;

        ImGuiIO io = ImGui.getIO();

        io.setKeysDown(event.getKeyCode(), false);
        System.out.println(event.getKeyCode() + " release");

        if(io.getWantTextInput() || (io.getWantCaptureKeyboard() && (
                event.getKeyCode() == GLFW_KEY_TAB ||
                event.getKeyCode() == GLFW_KEY_LEFT ||
                event.getKeyCode() == GLFW_KEY_RIGHT ||
                event.getKeyCode() == GLFW_KEY_UP ||
                event.getKeyCode() == GLFW_KEY_DOWN ||
                event.getKeyCode() == GLFW_KEY_PAGE_UP ||
                event.getKeyCode() == GLFW_KEY_PAGE_DOWN ||
                event.getKeyCode() == GLFW_KEY_HOME ||
                event.getKeyCode() == GLFW_KEY_END ||
                event.getKeyCode() == GLFW_KEY_INSERT ||
                event.getKeyCode() == GLFW_KEY_DELETE ||
                event.getKeyCode() == GLFW_KEY_BACKSPACE ||
                event.getKeyCode() == GLFW_KEY_SPACE ||
                event.getKeyCode() == GLFW_KEY_ENTER ||
                event.getKeyCode() == GLFW_KEY_KP_ENTER ||
                event.getKeyCode() == GLFW_KEY_A ||
                event.getKeyCode() == GLFW_KEY_C ||
                event.getKeyCode() == GLFW_KEY_V ||
                event.getKeyCode() == GLFW_KEY_X ||
                event.getKeyCode() == GLFW_KEY_Y ||
                event.getKeyCode() == GLFW_KEY_Z))
        ) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onScreenType(ScreenEvent.CharacterTyped.Pre event) {
        _inputCharacters += event.getCodePoint();

        if(ImGui.getIO().getWantTextInput())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onScreenClose(ScreenEvent.Closing event) {
        ImGuiIO io = ImGui.getIO();

        io.setKeyCtrl(false);
        io.setKeyShift(false);
        io.setKeyAlt(false);
        io.setKeySuper(false);

        io.setMouseDelta(0.0f, 0.0f);
        io.setMousePos(-1000.0f, -1000.0f);

        io.setMouseDown(new boolean[GLFW_MOUSE_BUTTON_LAST + 1]);
        io.setKeysDown(new boolean[512]);

        ImGui.setWindowFocus(null);
    }
}
