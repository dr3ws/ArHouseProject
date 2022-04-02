let CANVAS = document.querySelector('#follower');
let VRBUTTON = document.querySelector('button#vr');
let FSBUTTON = document.querySelector('button#fullscreen');
let DATA_REQUEST_ID = document.getElementById("data_request_id");
let DATA_REQUEST_NAMEOBJ = document.getElementById("data_request_nameobj");

let POLYFILL;
let SCENE, CAMERA, RENDERER;
let LOADER;
let EFFECT;
let VRDISPLAY;
let CONTROLS;
let LOADING_MANAGER;
let MTL_LOADER, OBJ_LOADER;
let OBJECT;

POLYFILL = new WebVRPolyfill();

const COLORLIGHT = 0xffffff;
const INTENSITYLIGHT = 0.5;
const PLANESIZESCENE = 1000;

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

RENDERER = new THREE.WebGLRenderer({
    CANVAS,
    alpha: true});
RENDERER.setPixelRatio(window.devicePixelRatio);
RENDERER.shadowMap.enabled = true;
RENDERER.shadowMapEnable = true;
CANVAS.appendChild(RENDERER.domElement);

CAMERA = new THREE.PerspectiveCamera(60, window.innerWidth / window.innerHeight, 1, 10000);
CAMERA.position.set(75, 60, 100);

// применение VR стерео рендеринга к рендереру
EFFECT = new THREE.VREffect(RENDERER);
EFFECT.setSize(CANVAS.clientWidth, CANVAS.clientHeight, false);

navigator.getVRDisplays().then(function (vrDisplays)
{
    if (vrDisplays.length) {
        VRDISPLAY = vrDisplays[0];
        // применение позиционных данных VR-гарнитуры к камере
        CONTROLS = new THREE.VRControls(CAMERA);
        document.querySelector('.not-active').style.display = "none";
        // цикл рендринга для VR
        VRDISPLAY.requestAnimationFrame(animate);
    } else {
        CONTROLS = new THREE.OrbitControls(CAMERA, CANVAS);
        CONTROLS.target.set(75, 25, 10);
        FSBUTTON.disabled = true;
        VRBUTTON.disabled = true;
        requestAnimationFrame(animate); // цикл рендринга
    }
});

const ambient = new THREE.AmbientLight(0xffffff, 0.7);
SCENE.add(ambient);

LIGHT = new THREE.DirectionalLight(COLORLIGHT, INTENSITYLIGHT); //  свет в определенном направлении
LIGHT.castShadow = true; // тень
LIGHT.position.set(20, 60, 20); // устанавливаем источник света
LIGHT.target.position.set(4, 0, 4); // устанавливаем направление света

SCENE.add(LIGHT);
SCENE.add(LIGHT.target);

{
    LOADING_MANAGER = new THREE.LoadingManager();
    MTL_LOADER = new THREE.MTLLoader(LOADING_MANAGER);
    OBJ_LOADER = new THREE.OBJLoader(LOADING_MANAGER);

    MTL_LOADER.setTexturePath('../../models/' + DATA_REQUEST_ID.innerHTML + '/');
    MTL_LOADER.load('../../models/' + DATA_REQUEST_ID.innerHTML + '/' + DATA_REQUEST_NAMEOBJ.innerHTML + '.mtl', (materials) => {
        materials.preload();
        OBJ_LOADER.setMaterials(materials);
        OBJ_LOADER.load('../../models/'+ DATA_REQUEST_ID.innerHTML + '/' + DATA_REQUEST_NAMEOBJ.innerHTML + '.obj', (object) => {
            object.rotation.set(0, 0, 0);
            OBJECT = object;
            SCENE.add(OBJECT);
        });
    });
}

function animate() {
    if (document.querySelector('.not-active').style.display === "none") {
        if (VRDISPLAY)
            VRDISPLAY.requestAnimationFrame(animate);
        else
            requestAnimationFrame(animate);
    }
    CONTROLS.update();
    EFFECT.render(SCENE, CAMERA);

    // if (CAMERA.position.y++) {
    //     document.querySelector('.event-move-cam-up').style.display = "block";
    // }
    //
    // if (CAMERA.position.y--) {
    //     document.querySelector('.event-move-cam-up').style.display = "none";
    // }
}

// function onResize() {
//     // The delay ensures the browser has a chance to layout
//     // the page and update the clientWidth/clientHeight.
//     // This problem particularly crops up under iOS.
//     if (!onResize.resizeDelay) {
//         onResize.resizeDelay = setTimeout(function () {
//             onResize.resizeDelay = null;
//             console.log('Resizing to %s x %s.', CANVAS.clientWidth, CANVAS.clientHeight);
//             EFFECT.setSize(CANVAS.clientWidth, CANVAS.clientHeight, false);
//             CAMERA.aspect = CANVAS.clientWidth / CANVAS.clientHeight;
//             CAMERA.updateProjectionMatrix();
//         }, 250);
//     }
// }
// function onVRDisplayPresentChange() {
//     console.log('onVRDisplayPresentChange');
//     onResize();
//     buttons.hidden = VRDISPLAY.isPresenting;
// }
//
// function onVRDisplayConnect(e) {
//     console.log('onVRDisplayConnect', (e.display || (e.detail && e.detail.display)));
// }
//
// // Resize the WebGL canvas when we resize and also when we change modes.
// window.addEventListener('resize', onResize);
// window.addEventListener('vrdisplaypresentchange', onVRDisplayPresentChange);
// window.addEventListener('vrdisplayconnect', onVRDisplayConnect);

FSBUTTON.addEventListener('click', function() {
    enterFullscreen(CANVAS); });

VRBUTTON.addEventListener('click', function() {
    if(VRBUTTON.textContent === 'GO VR') {
        VRDISPLAY.requestPresent([{ source: CANVAS }]);
        VRBUTTON.textContent = 'EXIT VR';
    } else {
        VRDISPLAY.exitPresent();
        VRBUTTON.textContent = 'GO VR';
    } });

function enterFullscreen (el) {
    if (el.requestFullscreen)
        el.requestFullscreen();
    // else if (el.mozRequestFullScreen)
    //     el.mozRequestFullScreen();
    // else if (el.webkitRequestFullscreen)
    //     el.webkitRequestFullscreen();
    // else if (el.msRequestFullscreen)
    //     el.msRequestFullscreen();
}