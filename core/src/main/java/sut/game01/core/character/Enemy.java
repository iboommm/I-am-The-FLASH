package sut.game01.core.character;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import playn.core.Layer;
import playn.core.util.Callback;
import playn.core.util.Clock;
import sut.game01.core.Setting;
import sut.game01.core.sprite.Sprite;
import sut.game01.core.sprite.SpriteLoader;
import tripleplay.game.ScreenStack;

/**
 * Created by GTX on 26/5/2559.
 */
public class Enemy {
    private Sprite sprite;
    private int spriteIndex = 0;
    private boolean hasLoaded = false;
    public static Body body;
    private ScreenStack ss;
    private float x;
    private float y;


    FixtureDef fixtureDef;
    public int stateJump = 0;


    public enum State{
        IDLE,WALK,JUMP
    };
    private Player.State state = Player.State.IDLE;

    private int e =0;

    public Enemy(final World world, final float x, final float y,int stateJump){
        this.x=x;
        this.y=y;

        this.stateJump = stateJump;

        sprite = SpriteLoader.getSprite("images/enemy.json");
        sprite.addCallback(new Callback<Sprite>() {
            @Override
            public void onSuccess(Sprite sprite) {
                sprite.setSprite(spriteIndex);
                sprite.layer().setOrigin(sprite.width()/2f,sprite.height()/2f);
                sprite.layer().setTranslation(x,y);
                Body body = initPhysicsBody(world,
                        Setting.M_PER_PIXEL * x,
                        Setting.M_PER_PIXEL * y);
                hasLoaded = true;
                System.out.println("Loaded");
                state = Player.State.IDLE;
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });


    }

    public Layer layer(){
        return sprite.layer();
    }

    private Body initPhysicsBody(World world, float x, float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position = new Vec2(0,0);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((sprite.layer().width()+20)* Setting.M_PER_PIXEL/2,
                sprite.layer().height()* Setting.M_PER_PIXEL/2);


        fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.3f;
        fixtureDef.friction = 0.1f;
        //fixtureDef.restitution = 0.5f;

        body.createFixture(fixtureDef);
        body.setLinearDamping(0.2f);
        body.setTransform(new Vec2(x,y),0f);
        return body;
    }

    public void update(int delta) {
        if(!hasLoaded)return;
        e+=delta;

        if(e>150){
            switch (state){
                case IDLE:
                    if(!(spriteIndex>=0&&spriteIndex<=7)){
                        spriteIndex=0;
                    }
                    break;
            }
            sprite.setSprite(spriteIndex);
            spriteIndex++;
            e=0;

        }
        if(stateJump == 0) {
            body.applyLinearImpulse(new Vec2(-10f, -18f), body.getPosition());
            stateJump++;
        }
    }


    public void paint(Clock clock){
        if(!hasLoaded)return;


        sprite.layer().setTranslation(
                (body.getPosition().x / Setting.M_PER_PIXEL),
                (body.getPosition().y / Setting.M_PER_PIXEL));

        sprite.layer().setRotation(body.getAngle());
    }
}
