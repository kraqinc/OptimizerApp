import com.orionn.optimizer.tweaks.TweakEngine;

import java.io.File;

public class MainTest {

    public static void main(String[] args) {

        File file = new File("tweaks_repo/gaming-mode.tws");

        TweakEngine.execute(file);

    }
}
