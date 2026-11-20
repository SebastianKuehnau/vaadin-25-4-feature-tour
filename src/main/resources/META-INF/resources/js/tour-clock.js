// An ES module loaded at runtime with @JavaScript(type = MODULE): not bundled by Vite.
const format = new Intl.DateTimeFormat(undefined, { timeStyle: 'medium' });

class TourClock extends HTMLElement {
  connectedCallback() {
    this.textContent = format.format(new Date());
    this.timer = setInterval(() => (this.textContent = format.format(new Date())), 1000);
  }

  disconnectedCallback() {
    clearInterval(this.timer);
  }
}

if (!customElements.get('tour-clock')) {
  customElements.define('tour-clock', TourClock);
}
export { TourClock };
