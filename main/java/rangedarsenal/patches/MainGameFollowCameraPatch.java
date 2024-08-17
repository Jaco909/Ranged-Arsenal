package rangedarsenal.patches;

import java.awt.geom.Point2D;

import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.Input;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.MainGameFollowCamera;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;
import necesse.engine.modLoader.annotations.ModMethodPatch;

@ModMethodPatch(target = MainGameFollowCamera.class, name = "tickCamera", arguments = {TickManager.class, MainGame.class, Client.class})

public class MainGameFollowCameraPatch {

    @Advice.OnMethodEnter(
            skipOn = Advice.OnNonDefaultValue.class
    )
    static boolean onEnter() {
        return true;
    }
    @Advice.OnMethodExit()
    static void onExit(@Argument(0) TickManager tickManager, @Argument(1) MainGame mainGame, @Argument(2) Client client) {
        PlayerMob player = client.getPlayer();
        if (player != null) {
            mainGame.getCamera().centerCamera(player.getDrawX(), player.getDrawY());
            if (mainGame.isRunning() && player.getSelectedItem() != null) {
                float zoomAmount = 0.0f;
                if (client.getPlayer().getSelectedItem().item.idData.getStringID().toLowerCase().equals("sniperrifle") && client.getPlayer().buffManager.hasBuff("SniperZoomBuff")) {
                    zoomAmount = 200f;
                } else if (client.getPlayer().getSelectedItem().item.idData.getStringID().toLowerCase().equals("awp") && client.getPlayer().buffManager.hasBuff("AWPZoomBuff")) {
                    zoomAmount = 470f;
                } else {
                    zoomAmount = client.getPlayer().getSelectedItem().item.zoomAmount();
                }
                if (zoomAmount != 0.0F) {
                    float xDir;
                    float yDir;
                    if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
                        xDir = ControllerInput.getAimX();
                        yDir = ControllerInput.getAimY();
                    } else {
                        GameWindow window = WindowManager.getWindow();
                        xDir = (float)window.mousePos().sceneX / (float)window.getSceneWidth() * 2.0F - 1.0F;
                        yDir = (float)window.mousePos().sceneY / (float)window.getSceneHeight() * 2.0F - 1.0F;
                    }
                    mainGame.getCamera().setPosition(mainGame.getCamera().getX() + (int)(xDir * zoomAmount), mainGame.getCamera().getY() + (int)(yDir * zoomAmount));
                }
            }
        }
        Point2D.Float cameraShake = client.getCurrentCameraShake();
        mainGame.getCamera().setPosition(mainGame.getCamera().getX() + (int)cameraShake.x, mainGame.getCamera().getY() + (int)cameraShake.y);
    }
}