import angular from "angular";
import { LinelistModule } from "./linelist.module";
import "../../../../sass/pages/project-linelist.scss";

const app = angular.module("irida");
app.requires.push(LinelistModule);
