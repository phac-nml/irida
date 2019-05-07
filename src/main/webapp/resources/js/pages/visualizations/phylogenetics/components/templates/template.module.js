import angular from "angular";
import { TemplateComponent } from "./template.comonent";
import { TemplateService } from "./template.service";

export const TemplateModule = angular
  .module("irida.vis.template", [])
  .service("TemplateService", TemplateService)
  .component("templateComponent", TemplateComponent).name;
