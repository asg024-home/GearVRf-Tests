package org.gearvrf.tester;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.jodah.concurrentunit.Waiter;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBehavior;
import org.gearvrf.GVRBillboard;
import org.gearvrf.GVRComponent;
import org.gearvrf.GVRComponentGroup;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.GVRTransform;
import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRCylinderSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.unittestutils.GVRTestUtils;
import org.gearvrf.unittestutils.GVRTestableActivity;
import org.joml.Vector3f;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class SceneObjectTests
{
    private static final String TAG = SceneObjectTests.class.getSimpleName();
    private GVRTestUtils mTestUtils;
    private Waiter mWaiter;
    private GVRSceneObject mRoot;
    private GVRMaterial mBlueMtl;
    private boolean mDoCompare = true;

    @Rule
    public ActivityTestRule<GVRTestableActivity> ActivityRule = new ActivityTestRule<GVRTestableActivity>(GVRTestableActivity.class);

    @After
    public void tearDown()
    {
        GVRScene scene = mTestUtils.getMainScene();
        if (scene != null)
        {
            scene.clear();
        }
    }

    @Before
    public void setUp() throws Exception
    {
        GVRTestableActivity activity = ActivityRule.getActivity();
        mTestUtils = new GVRTestUtils(activity);
        mTestUtils.waitForOnInit();
        mWaiter = new Waiter();
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        mWaiter.assertNotNull(scene);

        mBlueMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        mBlueMtl.setDiffuseColor(0, 0, 1, 1);
        mRoot = scene.getRoot();

        mWaiter.assertNotNull(mRoot);
    }

    @Test
    public void canClearEmptyScene() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();

        GVRTextViewSceneObject text = new GVRTextViewSceneObject(ctx, "Hello");
        scene.removeAllSceneObjects();
    }

    @Test
    public void canClearSceneWithStuff() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject sphere = new GVRSceneObject(ctx);
        GVRTextViewSceneObject text = new GVRTextViewSceneObject(ctx, "Hello");

        text.getTransform().setPosition(-1, 0, -2);
        sphere.getTransform().setPosition(1, 0, -2);

        final GVRSceneObject background = new GVRCubeSceneObject(ctx, false);
        background.getTransform().setScale(10, 10, 10);
        background.setName("background");
        scene.addSceneObject(background);

        mRoot.addChildObject(sphere);
        mRoot.addChildObject(text);
        text.setName("text");
        sphere.setName("sphere");
        mTestUtils.waitForSceneRendering();
        scene.clear();
        mWaiter.assertNull(scene.getSceneObjectByName("background"));
        mWaiter.assertNull(scene.getSceneObjectByName("sphere"));
        mWaiter.assertNull(scene.getSceneObjectByName("text"));
    }

    @Test
    public void canDisplaySpheres() throws Exception
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject sphere1 = new GVRSphereSceneObject(ctx, true, mBlueMtl);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);

        final GVRTexture tex = ctx.getAssetLoader().loadCubemapTexture(new GVRAndroidResource(ctx, R.raw.beach));
        final GVRMaterial cubeMapMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Cubemap.ID);
        cubeMapMtl.setMainTexture(tex);

        GVRSceneObject sphere2 = new GVRSphereSceneObject(ctx, false, cubeMapMtl);

        sphere1.getTransform().setPosition(0, 0, -4);
        sphere2.getTransform().setScale(20, 20, 20);
        sphere1.setName("sphere1");
        sphere2.setName("sphere2");
        mTestUtils.waitForAssetLoad();
        mRoot.addChildObject(sphere1);
        scene.addSceneObject(sphere2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("sphere2"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("sphere1"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplaySpheres", mWaiter, mDoCompare);
    }

    @Test
    public void canDisplayCubes() throws Exception
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRSceneObject cube1 = new GVRCubeSceneObject(ctx, true, mBlueMtl);
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);

        final GVRTexture tex = ctx.getAssetLoader().loadCubemapTexture(new GVRAndroidResource(ctx, R.raw.beach));
        final GVRMaterial cubeMapMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Cubemap.ID);
        cubeMapMtl.setMainTexture(tex);
        GVRSceneObject cube2 = new GVRCubeSceneObject(ctx, false, cubeMapMtl);

        cube1.getTransform().setPosition(0, 0, -4);
        cube1.setName("cube1");
        cube2.getTransform().setScale(20, 20, 20);
        cube2.setName("cube2");
        mTestUtils.waitForAssetLoad();
        mRoot.addChildObject(cube1);
        scene.addSceneObject(cube2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("cube2"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("cube1"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayCubes", mWaiter, mDoCompare);
    }


    @Test
    public void canDisplayCylinders() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.color_sphere));
        GVRSceneObject cylinder1 = new GVRCylinderSceneObject(ctx, true, mBlueMtl);
        GVRMaterial mtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Texture.ID);
        GVRSceneObject cylinder2 = new GVRCylinderSceneObject(ctx, false, mtl);

        mtl.setTexture("u_texture", tex);
        cylinder1.getTransform().setPosition(0, 0, -4);
        cylinder1.setName("cylinder1");
        cylinder2.getTransform().setScale(10, 10, 10);
        cylinder2.setName("cylinder2");
        mTestUtils.waitForAssetLoad();
        mRoot.addChildObject(cylinder1);
        scene.addSceneObject(cylinder2);
        mTestUtils.waitForXFrames(2);
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder1"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder2"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayCylinders", mWaiter, mDoCompare);
    }


    @Test
    public void canDisplayNonTextured() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        GVRMaterial redMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRMaterial greenMtl = new GVRMaterial(ctx, GVRMaterial.GVRShaderType.Phong.ID);
        GVRSceneObject cylinder1 = new GVRCylinderSceneObject(ctx, true, redMtl);
        GVRSceneObject cylinder2 = new GVRCylinderSceneObject(ctx, false, greenMtl);

        redMtl.setDiffuseColor(1, 0, 0, 1);
        greenMtl.setDiffuseColor(0, 1, 0, 1);
        cylinder1.getTransform().setPosition(0, 0, -4);
        cylinder1.setName("cylinder1");
        cylinder2.getTransform().setScale(10, 10, 10);
        cylinder2.setName("cylinder2");
        mRoot.addChildObject(cylinder1);
        scene.addSceneObject(cylinder2);
        mTestUtils.waitForXFrames(20);
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder1"));
        mWaiter.assertNotNull(scene.getSceneObjectByName("cylinder2"));
        mTestUtils.screenShot(getClass().getSimpleName(), "canDisplayNonTextured", mWaiter, mDoCompare);
    }

    @Test
    public void attachBillboard() throws TimeoutException {

        GVRContext ctx = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));

        GVRSceneObject quadObj1 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex);
        quadObj1.getTransform().setPosition(0.8f, 0, -2);
        quadObj1.getTransform().setRotationByAxis(30, 1,1,1);
        quadObj1.attachComponent(new GVRBillboard(ctx));
        scene.getMainCameraRig().addChildObject(quadObj1);

        GVRSceneObject quadObj2 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex);
        quadObj2.getTransform().setPosition(-1.0f, 0.8f, -2);
        quadObj2.getTransform().setRotationByAxis(-45, 0,1,0);
        quadObj2.attachComponent(new GVRBillboard(ctx, new Vector3f(0, 1, 0)));
        scene.getMainCameraRig().addChildObject(quadObj2);

        GVRSceneObject quadObj3 = new GVRSceneObject(ctx, 1.1f, 1.1f, tex);
        quadObj3.getTransform().setPosition(-0.5f, -0.8f, -1.4f);
        quadObj3.getTransform().setRotationByAxis(-45, 1,0,0);
        quadObj3.attachComponent(new GVRBillboard(ctx, new Vector3f(0, 1, -1)));
        scene.getMainCameraRig().addChildObject(quadObj3);
        mTestUtils.waitForAssetLoad();
        mTestUtils.waitForXFrames(10);
        mTestUtils.screenShot(getClass().getSimpleName(), "testBillboards", mWaiter, mDoCompare);
    }


    @Test
    public void attachBillboardCameraOffset() throws TimeoutException
    {
        GVRContext ctx  = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));

        scene.getMainCameraRig().getTransform().setPosition(0.5f, 1.0f, -0.4f);

        GVRSceneObject quadObj1 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj1.getTransform().setPosition(0.8f, 1.0f, -3);
        quadObj1.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj2 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj2.getTransform().setPosition(-0.8f, -1.0f, -3);
        quadObj2.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj3 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj3.getTransform().setPosition(0.8f, -1.0f, -3);
        quadObj3.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj4 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj4.getTransform().setPosition(-0.8f, 1.0f, -3);
        quadObj4.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj5 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj5.getTransform().setPosition(-1.5f, 0.0f, -3);
        quadObj5.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj6 = new GVRSceneObject(ctx, 0.8f, 0.8f, tex);
        quadObj6.getTransform().setPosition(1.5f, 0.0f, -3);
        quadObj6.attachComponent(new GVRBillboard(ctx));
        mTestUtils.waitForAssetLoad();

        mRoot.addChildObject(quadObj1);
        mRoot.addChildObject(quadObj2);
        mRoot.addChildObject(quadObj3);
        mRoot.addChildObject(quadObj4);
        mRoot.addChildObject(quadObj5);
        mRoot.addChildObject(quadObj6);

        mTestUtils.waitForXFrames(2);
        mTestUtils.screenShot(getClass().getSimpleName(), "testBillboardsCamOffset", mWaiter, mDoCompare);
    }

    @Test
    public void testBillboardOwnersScale() throws TimeoutException {

        GVRContext ctx = mTestUtils.getGvrContext();
        GVRScene scene = mTestUtils.getMainScene();
        TextureEventHandler texHandler = new TextureEventHandler(mTestUtils, 1);
        final float epsilon = 0.00001f;

        ctx.getEventReceiver().addListener(texHandler);
        GVRTexture tex = ctx.getAssetLoader().loadTexture(new GVRAndroidResource(ctx, R.drawable.colortex));
        GVRSceneObject quadObj1 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex);
        GVRTransform quadTrans1 = quadObj1.getTransform();

        quadObj1.setName("quadObj1");
        quadTrans1.setPosition(0.8f, 0, -2);
        quadTrans1.setRotationByAxis(30, 1,1,1);
        quadTrans1.setScale(5,6,7);
        quadObj1.attachComponent(new GVRBillboard(ctx));

        GVRSceneObject quadObj2 = new GVRSceneObject(ctx, 1.0f, 1.0f, tex);
        GVRTransform quadTrans2 = quadObj2.getTransform();

        quadObj2.setName("quadObj2");
        quadTrans2.setPosition(-1.0f, 0.8f, -2);
        quadTrans2.setRotationByAxis(-45, 0,1,0);
        quadTrans2.setScale(0.5f,0.6f,0.7f);
        quadObj2.attachComponent(new GVRBillboard(ctx, new Vector3f(0, 1, 0)));

        GVRSceneObject quadObj3 = new GVRSceneObject(ctx, 1.1f, 1.1f, tex);
        GVRTransform quadTrans3 = quadObj3.getTransform();

        quadObj3.setName("quadObj3");
        quadTrans3.setPosition(-0.5f, -0.8f, -1.4f);
        quadTrans3.setRotationByAxis(-45, 1,0,0);
        quadTrans3.setScale(1,1,1);
        quadObj3.attachComponent(new GVRBillboard(ctx, new Vector3f(0, 1, -1)));

        scene.addSceneObject(quadObj1);
        scene.addSceneObject(quadObj2);
        scene.addSceneObject(quadObj3);
        mTestUtils.waitForAssetLoad();

        mTestUtils.waitForXFrames(2);

        float xs = quadTrans1.getScaleX();
        float ys = quadTrans1.getScaleY();
        float zs = quadTrans1.getScaleZ();

        mWaiter.assertTrue(Math.abs(5 - xs) < epsilon);
        mWaiter.assertTrue(Math.abs(6 - ys) < epsilon);
        mWaiter.assertTrue(Math.abs(7 - zs) < epsilon);

        xs = quadTrans2.getScaleX();
        ys = quadTrans2.getScaleY();
        zs = quadTrans2.getScaleZ();
        mWaiter.assertTrue(Math.abs(0.5f - xs) < epsilon);
        mWaiter.assertTrue(Math.abs(0.6f - ys) < epsilon);
        mWaiter.assertTrue(Math.abs(0.7f - zs) < epsilon);

        xs = quadTrans3.getScaleX();
        ys = quadTrans3.getScaleY();
        zs = quadTrans3.getScaleZ();
        mWaiter.assertTrue(Math.abs(1 - xs) < epsilon);
        mWaiter.assertTrue(Math.abs(1 - ys) < epsilon);
        mWaiter.assertTrue(Math.abs(1 - zs) < epsilon);
   }

    private final static int MAX_COMPONENTS_IN_GROUP = 5;
    private final class TestBehavior extends GVRBehavior {
        public TestBehavior(GVRContext gvrContext) {
            super(gvrContext);
        }
        @Override
        public void onDrawFrame(float frameTime) {
            mTestComponentGroupLatch.countDown();
            stopListening();
        }
    }
    private final CountDownLatch mTestComponentGroupLatch = new CountDownLatch(MAX_COMPONENTS_IN_GROUP);

    @Test
    public void testComponentGroup() throws InterruptedException {
        final GVRContext ctx = mTestUtils.getGvrContext();
        final GVRScene scene = mTestUtils.getMainScene();

        GVRComponentGroup<GVRBehavior> group = new GVRComponentGroup<>(ctx, GVRBehavior.getComponentType());
        final GVRSceneObject so = new GVRSceneObject(ctx);
        for (int i = 0; i < MAX_COMPONENTS_IN_GROUP; ++i) {
            group.addChildComponent(new TestBehavior(ctx));
        }
        so.attachComponent(group);
        scene.addSceneObject(so);
        mWaiter.assertTrue(mTestComponentGroupLatch.await(10, TimeUnit.SECONDS));

        group = (GVRComponentGroup<GVRBehavior>)so.getComponent(GVRBehavior.getComponentType());
        mWaiter.assertTrue(MAX_COMPONENTS_IN_GROUP == group.getSize());
    }
}
