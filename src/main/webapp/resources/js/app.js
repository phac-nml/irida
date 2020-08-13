import "./pages/search/irida.search";
// Import css
import "../css/app.css";
// Font Awesome
import "@fortawesome/fontawesome-free/js/all";
/*
This will load notifications into the global namespace.  Remove this once all
files have been converted over to wekbpack builds.
 */
import "./modules/notifications";
import { CART } from "./utilities/events-utilities";
import { showNotification } from "./modules/notifications";
import { getCartCount } from "./apis/cart/cart";
// Galaxy Alert if in galaxy session
import "./components/Header/PageHeader";
import { setBaseUrl } from "./utilities/url-utilities";

/*
This is here since this has been updated to use a standard Event,
and not handled through angularjs.
 */
document.addEventListener(CART.UPDATED, (e) => {
  const { count, added, duplicate, existing } = e.detail;

  const counter = document.querySelector(".js-cart-count");
  if (+count > 0) {
    counter.style.cssText = "display: inline-block;";
    counter.innerHTML = count;
  } else {
    counter.style.cssText = "display: none;";
  }

  // Display notifications
  if (added) {
    showNotification({
      text: added,
    });
  }

  if (duplicate) {
    showNotification({
      text: duplicate,
      type: "warning",
    });
  }

  if (existing) {
    showNotification({
      text: existing,
      type: "info",
    });
  }
});

/**
 * Initialize the cart
 */
getCartCount().then((count) => {
  const event = new CustomEvent(CART.UPDATED, { detail: count });
  document.dispatchEvent(event);
});

/*
Since IRIDA can be run on a servlet path, we need to make sure that all requests
get the correct base url.
 */
const xmlHttpRequestOpen = window.XMLHttpRequest.prototype.open;

function openBaseUrlModifier(method, url, async) {
  const newUrl = setBaseUrl(url);
  /*
  Call the original open method with the new url.
   */
  return xmlHttpRequestOpen.apply(this, [method, newUrl, async]);
}

window.XMLHttpRequest.prototype.open = openBaseUrlModifier;
