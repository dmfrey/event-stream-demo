
export default function toastTemplate( text, buttons ) {

    return `<div class="toast">
              <div class="toast-content">${text}</div>
              ${ buttons.map( btn => `<button class="unbutton">${btn}</button>` )}
            </div>`;
}