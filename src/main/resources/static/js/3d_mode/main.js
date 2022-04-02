let DATA_REQUEST_PATHPROJECT = document.getElementById("data-request-pathproject");
if (DATA_REQUEST_PATHPROJECT.textContent !== '') {
    document.querySelector('.not-active').style.display = "none";
}
if (DATA_REQUEST_PATHPROJECT.textContent === '') {
    document.querySelector('.navbar').style.width = "100%";
}

let CANVAS = document.querySelector('#follower');
let DATA_REQUEST_ID = document.getElementById("data-request-id");
let DATA_REQUEST_NAMEPROJECT = document.getElementById("data-request-nameproject");

let SCENE, CAMERA, RENDERER;
let PLANE;
let LOADER;
let LOADING_MANAGER;
let MTL_LOADER, OBJ_LOADER;
let CONTROLS;
let OBJECT;
let LIGHT;

const COLORLIGHT = 0xffffff;
const INTENSITYLIGHT = 0.5;
const PLANESIZESCENE = 1000;

class ColorGUIHelper {
    constructor(object, prop) {
        this.object = object;
        this.prop = prop;
    }
    get value() {
        return `#${this.object[this.prop].getHexString()}`;
    }
    set value(hexString) {
        this.object[this.prop].set(hexString);
    }
}

class AxesGUIHelper {
    constructor(node) {
        const axes = new THREE.AxesHelper(20);
        axes.renderOrder = 2;
        node.add(axes);
        this.axes = axes;
        this.visible = false;
    }
    get visible() {
        return this._visible;
    }
    set visible(v) {
        this._visible = v;
        this.axes.visible = v;
    }
}

// class DimensionGUIHelper {
//     constructor(obj, minProp, maxProp) {
//         this.obj = obj;
//         this.minProp = minProp;
//         this.maxProp = maxProp;
//     }
//     get value() {
//         return this.obj[this.maxProp] * 2;
//     }
//     set value(v) {
//         this.obj[this.maxProp] = v / 2;
//         this.obj[this.minProp] = v / -2;
//     }
// }
//
// class MinMaxGUIHelper {
//     constructor(obj, minProp, maxProp, minDif) {
//         this.obj = obj;
//         this.minProp = minProp;
//         this.maxProp = maxProp;
//         this.minDif = minDif;
//     }
//     get min() {
//         return this.obj[this.minProp];
//     }
//     set min(v) {
//         this.obj[this.minProp] = v;
//         this.obj[this.maxProp] = Math.max(this.obj[this.maxProp], v + this.minDif);
//     }
//     get max() {
//         return this.obj[this.maxProp];
//     }
//     set max(v) {
//         this.obj[this.maxProp] = v;
//         this.min = this.min;
//     }
// }

main();

function main() {
    initScene();
    initLights();
    initCamera();
    initRenderer();
    initLoaders();
    initControls();
    if (DATA_REQUEST_PATHPROJECT.textContent !== '')
        initControlPanel();
    loadModel();
}

// сцена
function initScene() {
    SCENE = new THREE.Scene();
    SCENE.fog = new THREE.Fog(0xa0a0a0, 1, 500);
    SCENE.background = new THREE.Color(0xa0a0a0);

    {
        LOADER = new THREE.TextureLoader();
        let texture = LOADER.load('../../images/texture_plane.png');
        texture.wrapS = THREE.RepeatWrapping;
        texture.wrapT = THREE.RepeatWrapping;
        texture.magFilter = THREE.NearestFilter;
        texture.repeat.set(50, 50);

        let planeGeometry = new THREE.PlaneGeometry(PLANESIZESCENE, PLANESIZESCENE);
        let planeMaterial = new THREE.MeshPhongMaterial({
            map: texture,
            side: THREE.DoubleSide,
        });
        PLANE = new THREE.Mesh(planeGeometry, planeMaterial);

        PLANE.rotation.x = -Math.PI / 2;
        PLANE.position.x = 0;
        PLANE.position.y = -0.5;
        PLANE.position.z = 0;

        PLANE.receiveShadow = true; // объект получает тени
        SCENE.add(PLANE);
    }
}

// источник света
function initLights() {
    const ambient = new THREE.AmbientLight(0xffffff, 0.7); // заполняющий свет
    SCENE.add(ambient);

    LIGHT = new THREE.DirectionalLight(COLORLIGHT, INTENSITYLIGHT); //  свет в определенном направлении
    LIGHT.castShadow = true; // тень
    LIGHT.position.set(80, 60, -20); // устанавливаем источник света
    LIGHT.target.position.set(45, 0, 30); // устанавливаем направление света

    SCENE.add(LIGHT);
    SCENE.add(LIGHT.target);
}

// камера
function initCamera() {
    CAMERA = new THREE.PerspectiveCamera(
        45, // fov - поле зрения. Определяет угол, который можно видеть вокруг центра камеры
        window.innerWidth / window.innerHeight, // (aspect ratio) пропорция (соотношение ширины к высоте экрана)
        1, // (near) минимальное расстояние от камеры, которое попадает в рендеринг
        2000 // (far) максимальное расстояние от камеры, которое попадает в рендеринг
    );
    CAMERA.position.set(110, 80, 100);
}

// отрисовка изображения
function initRenderer() {
    RENDERER = new THREE.WebGLRenderer({
        CANVAS,
        alpha: true});
    RENDERER.setPixelRatio(window.devicePixelRatio);
    RENDERER.setSize(window.innerWidth, window.innerHeight);
    RENDERER.shadowMap.enabled = true;
    RENDERER.shadowMapEnable = true;
    CANVAS.appendChild(RENDERER.domElement);
}

// загрузчики
function initLoaders() {
    LOADING_MANAGER = new THREE.LoadingManager();
    MTL_LOADER = new THREE.MTLLoader(LOADING_MANAGER);
    OBJ_LOADER = new THREE.OBJLoader(LOADING_MANAGER);
}

// событие вращения
function initControls() {
    CONTROLS = new THREE.OrbitControls(CAMERA, CANVAS);
    CONTROLS.target.set(110, 25, 10);
    CONTROLS.maxPolarAngle = Math.PI * 1.5 / 4;
    CONTROLS.zoomSpeed = 3;
    CONTROLS.update();
}

// загрузка модели
function loadModel() {
    MTL_LOADER.setTexturePath('../../models/' + DATA_REQUEST_ID.innerHTML + '/');
    MTL_LOADER.load('../../models/' + DATA_REQUEST_ID.innerHTML + '/' + DATA_REQUEST_NAMEPROJECT.innerHTML + '.mtl', (materials) => {
        materials.preload();
        OBJ_LOADER.setMaterials(materials);
        OBJ_LOADER.load('../../models/' + DATA_REQUEST_ID.innerHTML + '/' + DATA_REQUEST_NAMEPROJECT.innerHTML + '.obj', (object) => {
            object.scale.set(15, 15, 15);
            object.rotation.set(0, 0, 0);
            object.castShadow = true; // объект отбрасывает тени
            object.receiveShadow = true; // объект получает тени
            OBJECT = object;
            SCENE.add(OBJECT);
        });
    });
}

// панель управления
function initControlPanel() {
    let cameraHelper = new THREE.CameraHelper(LIGHT.shadow.camera);
    // SCENE.add(cameraHelper);

    let directionalLightHelper = new THREE.DirectionalLightHelper(LIGHT);
    // SCENE.add(directionalLightHelper);

    function updateCamera() {
        LIGHT.target.updateMatrixWorld();
        directionalLightHelper.update();
        // обновление матрицы проекции теневой камеры света
        LIGHT.shadow.camera.updateProjectionMatrix();
        // обновление cameraHelper, для того, чтобы показать теневую камеру света
        cameraHelper.update();

        requestAnimationFrame(animate);
    }
    updateCamera();

    let gui = new dat.GUI();
    gui.close();
    gui.add(LIGHT, 'intensity', 0, 2, 0.01).name('Яркость света').onChange(requestRenderIfNotRequested);
    gui.addColor(new ColorGUIHelper(LIGHT, 'color'), 'value').name('Цвет').onChange(requestRenderIfNotRequested);

    // makeShadow(gui, LIGHT.shadow.camera, 'Shadow camera', updateCamera);
    makeXYZLight(gui, LIGHT.position, 'Позиция света', updateCamera);
    makeXYZLightTarget(gui, LIGHT.target.position, 'Направление света', updateCamera);
    makeXYZCamera(gui, CAMERA.position, 'Положение амера', updateCamera);
}

let renderRequested = false;
function animate() {
    renderRequested = undefined;

    if (resizeRendererToDisplaySize(RENDERER)) {
        const canvas = RENDERER.domElement;
        CAMERA.aspect = canvas.clientWidth / canvas.clientHeight;
        CAMERA.updateProjectionMatrix();
    }

    if (CAMERA.position.y < 21)
        console.log("Дальше нельзя!");
    else {
        if (DATA_REQUEST_PATHPROJECT.textContent !== '')
            RENDERER.render(SCENE, CAMERA);
    }
}

// запрос рендеринга, если он не запрошен
function requestRenderIfNotRequested() {
    if (!renderRequested) {
        renderRequested = true;
        requestAnimationFrame(animate);
    }
}

// CANVAS.addEventListener('click', requestRenderIfNotRequested);
CONTROLS.addEventListener('change', requestRenderIfNotRequested);
window.addEventListener('resize', requestRenderIfNotRequested);

// function animate() {
//     if (DATA_REQUEST_PATHPROJECT.textContent !== '') {
//         if (CAMERA.position.y < 21)
//             console.log("Дальше нельзя!");
//         else
//             RENDERER.render(SCENE, CAMERA);
//         requestAnimationFrame(animate); // цикл рендринга
//     }
// }

// function makeShadow(gui, vector, name, onChangeFn) {
//     const shadowFolder = gui.addFolder(name);
//     shadowFolder.add(new DimensionGUIHelper(vector, 'left', 'right'), 'value', 1, 100)
//         .name('width')
//         .onChange(onChangeFn);
//     shadowFolder.add(new DimensionGUIHelper(vector, 'bottom', 'top'), 'value', 1, 100)
//         .name('height')
//         .onChange(onChangeFn);
//     const minMaxGUIHelper = new MinMaxGUIHelper(vector, 'near', 'far', 0.1);
//     shadowFolder.add(minMaxGUIHelper, 'min', 0.1, 500, 0.1).name('near').onChange(onChangeFn);
//     shadowFolder.add(minMaxGUIHelper, 'max', 0.1, 500, 0.1).name('far').onChange(onChangeFn);
//     shadowFolder.add(vector, 'zoom', 0.01, 1.5, 0.01).onChange(onChangeFn);
// }

function makeXYZLight(gui, vector, name, onChangeFn) {
    const lightFolder = gui.addFolder(name);

    const helper = new AxesGUIHelper(LIGHT);
    lightFolder.add(helper, 'visible').name('Оси').onChange(onChangeFn);

    lightFolder.add(vector, 'x', -150, 150).onChange(onChangeFn);
    lightFolder.add(vector, 'y', 0, 150).onChange(onChangeFn);
    lightFolder.add(vector, 'z', -150, 150).onChange(onChangeFn);
}

function makeXYZLightTarget(gui, vector, name, onChangeFn) {
    const lightFolder = gui.addFolder(name);

    lightFolder.add(vector, 'x', -150, 150).onChange(onChangeFn);
    lightFolder.add(vector, 'y', 0, 150).onChange(onChangeFn);
    lightFolder.add(vector, 'z', -150, 150).onChange(onChangeFn);
}

function makeXYZCamera(gui, vector, name, onChangeFn) {
    const cameraFolder = gui.addFolder(name);
    cameraFolder.add(vector, "x", -150, 150).onChange(onChangeFn);
    cameraFolder.add(vector, "y", 21, 150).onChange(onChangeFn);
    cameraFolder.add(vector, "z", -150, 150).onChange(onChangeFn);
}

function resizeRendererToDisplaySize(renderer) {
    const canvas = renderer.domElement;
    const width = canvas.clientWidth;
    const height = canvas.clientHeight;
    const needResize = canvas.width !== width || canvas.height !== height;
    if (needResize)
        renderer.setSize(width, height, false);
    return needResize;
}