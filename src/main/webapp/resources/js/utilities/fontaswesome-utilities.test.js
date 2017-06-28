import test from "ava";
import {createIcon, ICONS} from "./fontawesome-utilities";

test("Should create proper 'i' element", t => {
  t.is('<i class="fa fa-download"></i>', createIcon({icon: ICONS.download}));
  t.is('<i class="fa fa-download fa-fw"></i>', createIcon({icon: ICONS.download, fixed: true}));
});
