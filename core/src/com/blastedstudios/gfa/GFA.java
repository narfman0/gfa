package com.blastedstudios.gfa;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

public class GFA extends ApplicationAdapter {
	private Environment environment;
	private Camera camera;
	HeightField field;
	Renderable ground;
	public CameraInputController cameraController;
	public static final float MOVE_SPEED = 10f, TURN_RATE = 100f;
	public ModelBatch modelBatch;
	ModelInstance instance;
	Model model;
	
	@Override
	public void create () {
		camera = createCamera();
		modelBatch = new ModelBatch();
		cameraController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(cameraController);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(Gdx.files.internal("ship/ship.obj"));
		instance = new ModelInstance(model);

//		Pixmap data = new Pixmap(Gdx.files.internal("data/g3d/heightmap.png"));
//		field = new HeightField(true, data, true,
//				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
	}

	@Override
	public void render () {
		processInput(Gdx.graphics.getDeltaTime());
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(camera);
		modelBatch.render(instance, environment);
		modelBatch.end();
	}

	private void processInput (float dt) {
		Vector3 movement = new Vector3();
		if (Gdx.input.isKeyPressed(Input.Keys.W))
			movement.add(camera.direction.cpy().scl(dt));
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			movement.add(camera.direction.cpy().scl(-dt));
		camera.translate(movement);
		if (Gdx.input.isKeyPressed(Input.Keys.A))
			camera.rotate(TURN_RATE * dt, 0, 1, 0);
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			camera.rotate(-TURN_RATE * dt, 0, 1, 0);
		camera.update();
	}

	public static Camera createCamera(){
		Camera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 1f;
		camera.far = 300f;
		camera.update();
		camera.translate(0, 1, -2);
		camera.direction.set(0, 0, 1);
		camera.update();
		return camera;
	}
}
