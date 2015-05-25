function tableDrawn() {
  'use strict';

  var h = window.innerHeight,
    el = document.getElementsByClassName('dataTables_scrollBody')[0],
    viewportOffset = el.getBoundingClientRect();
  el.style.height = h - viewportOffset.top - 60 + 'px';
}

window.onresize = tableDrawn;
