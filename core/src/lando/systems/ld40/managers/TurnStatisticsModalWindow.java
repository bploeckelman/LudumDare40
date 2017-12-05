package lando.systems.ld40.managers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld40.ui.ModalWindow;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld40.utils.Assets;
import lando.systems.ld40.world.Statistics;


public class TurnStatisticsModalWindow extends ModalWindow {

    private static final Color text_msg_color = new Color(235f / 255f, 255f / 255f, 218f / 255f, 1f);
    private static final Color text_msg_bg_color = new Color(71f / 255f, 71f / 255f, 87f / 255f, 1f);
    private static final Color text_color = new Color(217f / 255f, 126f / 255f, 0f, 1f);
    private static final float text_scale = 0.3f;

    private Rectangle turnStatisticsRect;

    public TurnStatisticsModalWindow(OrthographicCamera camera) {
        super(camera);
        this.turnStatisticsRect = new Rectangle();

    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        batch.setShader(Assets.fontShader);
        {
            final float title_text_scale = 0.5f;
            final float target_width = modalRect.width;
            Assets.font.getData().setScale(title_text_scale);
            Assets.fontShader.setUniformf("u_scale", title_text_scale);
            Assets.layout.setText(Assets.font, "Turn Statistics",
                    text_color, target_width, Align.center, true);
            Assets.font.draw(batch, Assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top);
            Assets.font.setColor(Color.WHITE);
            Assets.font.getData().setScale(1f);
            Assets.fontShader.setUniformf("u_scale", 1f);
        }

        batch.setShader(Assets.fontShader);
        {
            final float title_text_scale = 0.5f;
            final float target_width = modalRect.width;
            Assets.font.getData().setScale(title_text_scale);
            Assets.fontShader.setUniformf("u_scale", title_text_scale);
            Assets.layout.setText(Assets.font, "Trash Gained: " + Statistics.getStatistics().getCurrentTurnStatistics().garbageGenerated,
                    text_color, target_width, Align.left, true);
            Assets.font.draw(batch, Assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top * 10f);
            Assets.font.setColor(Color.WHITE);
            Assets.font.getData().setScale(1f);
            Assets.fontShader.setUniformf("u_scale", 1f);
        }

        batch.setShader(Assets.fontShader);
        {
            final float title_text_scale = 0.5f;
            final float target_width = modalRect.width;
            Assets.font.getData().setScale(title_text_scale);
            Assets.fontShader.setUniformf("u_scale", title_text_scale);
            Assets.layout.setText(Assets.font, "Trash Hauled: " + Statistics.getStatistics().getCurrentTurnStatistics().garbageHauled,
                    text_color, target_width, Align.left, true);
            Assets.font.draw(batch, Assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top * 15f);
            Assets.font.setColor(Color.WHITE);
            Assets.font.getData().setScale(1f);
            Assets.fontShader.setUniformf("u_scale", 1f);
        }

        batch.setShader(Assets.fontShader);
        {
            final float title_text_scale = 0.5f;
            final float target_width = modalRect.width;
            Assets.font.getData().setScale(title_text_scale);
            Assets.fontShader.setUniformf("u_scale", title_text_scale);
            Assets.layout.setText(Assets.font, "Garbage in Landfill: " + Statistics.getStatistics().getCurrentTurnStatistics().garbageInLandFills,
                    text_color, target_width, Align.left, true);
            Assets.font.draw(batch, Assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top * 20f);
            Assets.font.setColor(Color.WHITE);
            Assets.font.getData().setScale(1f);
            Assets.fontShader.setUniformf("u_scale", 1f);
        }

        batch.setShader(Assets.fontShader);
        {
            final float title_text_scale = 0.5f;
            final float target_width = modalRect.width;
            Assets.font.getData().setScale(title_text_scale);
            Assets.fontShader.setUniformf("u_scale", title_text_scale);
            Assets.layout.setText(Assets.font, "Money: " + Statistics.getStatistics().getCurrentTurnStatistics().money,
                    text_color, target_width, Align.left, true);
            Assets.font.draw(batch, Assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top * 25f);
            Assets.font.setColor(Color.WHITE);
            Assets.font.getData().setScale(1f);
            Assets.fontShader.setUniformf("u_scale", 1f);
        }

        batch.setShader(Assets.fontShader);
        {
            final float title_text_scale = 0.5f;
            final float target_width = modalRect.width;
            Assets.font.getData().setScale(title_text_scale);
            Assets.fontShader.setUniformf("u_scale", title_text_scale);
            Assets.layout.setText(Assets.font, "Number of Addons: " + Statistics.getStatistics().getCurrentTurnStatistics().addons,
                    text_color, target_width, Align.left, true);
            Assets.font.draw(batch, Assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top * 30f);
            Assets.font.setColor(Color.WHITE);
            Assets.font.getData().setScale(1f);
            Assets.fontShader.setUniformf("u_scale", 1f);
        }

    }


}






