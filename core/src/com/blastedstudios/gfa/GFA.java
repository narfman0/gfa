package com.blastedstudios.gfa;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;

public class GFA extends ApplicationAdapter {
	private Environment environment;
	private Camera camera;
	private Texture texture;
	HeightField field;
	Renderable ground;
	public CameraInputController cameraController;
	public static final float TURN_RATE = 100f;
	public ModelBatch modelBatch;
	ModelInstance instance;
	Model model;

	@Override
	public void create () {
		initCamera();
		modelBatch = new ModelBatch();
		cameraController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(cameraController);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(Gdx.files.internal("ship/ship.obj"));
		instance = new ModelInstance(model);
		initHeightmap();
	}

	@Override
	public void render () {
		processInput(Gdx.graphics.getDeltaTime());
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(camera);
		modelBatch.render(instance, environment);
		modelBatch.render(ground);
		modelBatch.end();
	}

	private void processInput (float dt) {
		Vector3 movement = new Vector3();
		if (Gdx.input.isKeyPressed(Input.Keys.W))
			movement.add(camera.direction.cpy().scl(dt));
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			movement.add(camera.direction.cpy().scl(-dt));
		if (Gdx.input.isKeyPressed(Input.Keys.Q))
			movement.add(new Vector3(0, 1, 0).scl(dt));
		if (Gdx.input.isKeyPressed(Input.Keys.E))
			movement.add(new Vector3(0, 1, 0).scl(-dt));
		camera.translate(movement);
		if (Gdx.input.isKeyPressed(Input.Keys.A))
			camera.rotate(TURN_RATE * dt, 0, 1, 0);
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			camera.rotate(-TURN_RATE * dt, 0, 1, 0);
		camera.update();
	}

	void initCamera(){
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = .1f;
		camera.far = 300f;
		camera.update();
		camera.translate(0, 1, -2);
		camera.direction.set(0, 0, 1);
		camera.update();
	}

	void initHeightmap(){
		texture = new Texture(Gdx.files.internal("grass.jpg"));

		Pixmap data = new Pixmap(Gdx.files.internal("heightmap.png"));
		field = new HeightField(true, data, true,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
		data.dispose();
		field.corner00.set(-10f, 0, -10f);
		field.corner10.set(10f, 0, -10f);
		field.corner01.set(-10f, 0, 10f);
		field.corner11.set(10f, 0, 10f);
		field.color00.set(0, 0, 1, 1);
		field.color01.set(0, 1, 1, 1);
		field.color10.set(1, 0, 1, 1);
		field.color11.set(1, 1, 1, 1);
		field.magnitude.set(0f, 5f, 0f);
		field.update();


		ground = new Renderable();
		ground.environment = environment;
		ground.meshPart.mesh = field.mesh;
		ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
		ground.meshPart.offset = 0;
		ground.meshPart.size = field.mesh.getNumIndices();
		ground.meshPart.update();
		ground.material = new Material(TextureAttribute.createDiffuse(texture));
	}

	@Override
	public void dispose () {
		super.dispose();
		texture.dispose();
		field.dispose();
		model.dispose();
		modelBatch.dispose();
	}
}
