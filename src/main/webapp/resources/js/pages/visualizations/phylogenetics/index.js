import angular from "angular";
import { AppModule } from "./components/app/app.module";

const app = angular.module("irida");
app.requires.push(AppModule);
