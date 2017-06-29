import test from 'ava';
import {createIcon, ICONS} from './fontawesome-utilities';

test("Should create proper 'i' element without a fixed width", t => {
  const icon = createIcon({icon: ICONS.download});
  t.true(icon.classList.contains('fa'), 'Icon should have the default font-awesome class');
  t.true(icon.classList.contains('fa-download'), "Icon should have the download class");
  t.false(icon.classList.contains('fa-fw'), 'Icon should not have a fixed width class');
});

test('Should create a proper \'i\' element with fixed width', t => {
  const icon = createIcon({icon: ICONS.download, fixed: true});
  t.true(icon.classList.contains('fa'), 'Icon should have the default font-awesome class');
  t.true(icon.classList.contains('fa-download'), "Icon should have the download class");
  t.true(icon.classList.contains('fa-fw'), 'Icon should not have a fixed width class');
});
