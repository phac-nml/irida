import {createIcon, ICONS} from "./fontawesome-utilities";

test("Should create proper 'i' element without a fixed width", () => {
  const icon = createIcon({icon: ICONS.download});
  expect(icon.classList.contains('fa')).toBeTruthy;
  expect(icon.classList.contains('fa-download')).toBeTruthy;
  expect(icon.classList.contains('fa-fw')).not.toBeTruthy;
});

test("Should create a proper 'i' element with fixed width", () => {
  const icon = createIcon({icon: ICONS.download, fixed: true});
  expect(icon.classList.contains('fa')).toBeTruthy;
  expect(icon.classList.contains('fa-download')).toBeTruthy;
  expect(icon.classList.contains('fa-fw')).toBeTruthy;
});
