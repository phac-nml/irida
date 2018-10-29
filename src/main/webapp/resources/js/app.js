import angular from "angular";
import "./modules/cart/irida.cart";
import { IridaSession } from "./services/IridaSession";
import "./pages/search/irida.search";
// Import css
import "../sass/app.scss";
// Font Awesome
import "@fortawesome/fontawesome-free/js/all";
/*
This will load notifications into the global namespace.  Remove this once all
files have been converted over to wekbpack builds.
 */
import "./modules/notifications";

const deps = ["ngAria", "ui.bootstrap", "irida.cart", IridaSession];

const app = angular.module("irida", deps);

export default app;
