(function($) {
  /***
   * remote model for SlickGrid fetching analysis output file lines
   * @param id
   * @param initSeek
   * @return {{data: {length: number}, clear: clear, isDataLoaded: isDataLoaded, ensureData: ensureData, reloadData: reloadData, setSort: setSort, setSearch: setSearch, onDataLoading, onDataLoaded}}
   */
  function RemoteModel(id, initSeek) {
    // private
    const baseUrl = `${window.PAGE.URLS.base}${window.PAGE.ID}/outputs/${id}`;
    let PAGESIZE = 100;
    let data = { length: 0 };
    let seek = 0; //initSeek || 0;
    let h_request = null;
    let req = null; // ajax request

    // events
    const onDataLoading = new Slick.Event();
    const onDataLoaded = new Slick.Event();

    function init() {}

    function isUndefinedOrNull(x) {
      return x === undefined || x === null;
    }

    function isDataLoaded(from, to) {
      for (let i = from; i <= to; i++) {
        if (isUndefinedOrNull(data[i])) {
          return false;
        }
      }
      return true;
    }

    function clear() {
      for (const key in data) {
        delete data[key];
      }
      data.length = 0;
    }

    function ensureData(from, to) {
      if (req) {
        req.abort();
        for (let i = req.from; i <= req.to; i++) {
          data[i] = undefined;
        }
      }

      if (from < 0) {
        from = 0;
      }

      if (data.length > 0) {
        to = Math.min(to, data.length);
      }

      var fromPage = Math.floor(from / PAGESIZE);
      var toPage = Math.floor(to / PAGESIZE);

      while (data[fromPage * PAGESIZE] !== undefined && fromPage < toPage)
        fromPage++;

      while (data[toPage * PAGESIZE] !== undefined && fromPage < toPage)
        toPage--;

      if (
        fromPage > toPage ||
        (fromPage === toPage && data[fromPage * PAGESIZE] !== undefined)
      ) {
        onDataLoaded.notify({ from: from, to: to });
        return;
      }

      var recStart = fromPage * PAGESIZE;
      var recCount = (toPage - fromPage) * PAGESIZE + PAGESIZE;

      const params = { start: recStart, limit: recCount };

      if (seek > 0) {
        params.seek = seek;
        params.start = 0;
        params.limit = PAGESIZE;
      }

      console.log("URL", baseUrl);
      console.log("params", params);
      const url = `${baseUrl}?${$.param(params)}`;
      console.log("url", url);
      if (h_request != null) {
        clearTimeout(h_request);
      }

      h_request = setTimeout(function() {
        for (let i = recStart; i < recStart + recCount; i++) {
          if (!data.hasOwnProperty(i)) {
            data[i] = null; // null indicates a 'requested but not available yet'
          } else {
            console.warn("already exists!", i, data[i]);
          }
        }

        onDataLoading.notify({ from: from, to: to });

        req = $.ajax({
          url: url,
          success: function onSuccess(resp) {
            // const from = recStart;
            const from = resp.start;
            const to = resp.lines.length;
            console.log("SUCCESS!!!", resp);
            // seek = resp.filePointer;

            let headers = resp.firstLine.split("\t");

            for (let i = 0; i < resp.lines.length; i++) {
              let idx = from + i;
              if (data[idx] === undefined || data[idx] === null) {
                data[idx] = {};
              }
              data[idx].id = idx + "";
              const row = resp.lines[i].split("\t");
              for (let j = 0; j < headers.length; j++) {
                data[idx][j + ""] = row[j];
              }
            }
            if (resp.lines.length < PAGESIZE) {
              for (const key in data) {
                if (data[key] === undefined || data[key] === null) {
                  delete data[key];
                }
              }
            }

            data.length = Object.keys(data).length - 1;

            req = null;

            onDataLoaded.notify({ from: from, to: to });
          },
          error: function() {
            console.warn(recStart, recCount);
            console.warn("error loading lines " + from + " to " + to);
          }
        });
        req.from = from;
        req.to = to;
      }, 50);
    }

    function reloadData(from, to) {
      for (let i = from; i <= to; i++) delete data[i];

      ensureData(from, to);
    }

    init();

    return {
      // properties
      data: data,

      // methods
      clear: clear,
      isDataLoaded: isDataLoaded,
      ensureData: ensureData,
      reloadData: reloadData,

      // events
      onDataLoading: onDataLoading,
      onDataLoaded: onDataLoaded
    };
  }

  // Slick.Data.RemoteModel
  $.extend(true, window, { Slick: { Data: { RemoteModel: RemoteModel } } });
})(jQuery);
