import { ImageManipulator } from '@capacitor-community/image-manipulator';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    ImageManipulator.echo({ value: inputValue })
}
