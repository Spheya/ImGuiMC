package gay.tami.imguimc;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FreeCursorScreen extends Screen {
    protected FreeCursorScreen() {
        super(Component.empty());
    }
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(ImGuiMC.s_freeCursorKey.getKey().getValue() == pKeyCode) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
