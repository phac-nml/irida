(function(factory) {
  // Default DataTables plugin setup.
  if (typeof define === "function" && define.amd) {
    // AMD
    define(["jquery", "datatables.net"], function($) {
      return factory($, window, document);
    });
  } else if (typeof exports === "object") {
    // CommonJS
    module.exports = function(root, $) {
      if (!root) {
        root = window;
      }

      if (!$ || !$.fn.dataTable) {
        $ = require("datatables.net")(root, $).$;
      }

      return factory($, root, root.document);
    };
  } else {
    // Browser
    factory(jQuery, window, document);
  }
})(function($, window, document, undefined) {
  const DataTable = $.fn.dataTable;

  // Version information for debugger
  DataTable.select = {
    version: "0.0.1"
  };

  /**
   * Internationalization Helper
   */
  function i18n(label, def) {
    return function(dt) {
      return dt.i18n(label, def);
    };
  }

  /**
   * Initialize the plugin.
   * @param {object} dt - the current DataTable
   */
  DataTable.select.init = function(dt) {
    const ctx = dt.settings()[0];
    // Get the configuration for the selection from initialization object.
    const opts = ctx.oInit.select;

    // Default items
    const selector = "input[type=checkbox]";
    const selected = new Map();
    let currentId = undefined;

    ctx._select = Object.assign({}, opts);

    dt.select.selector(selector);
    dt.select.selected(selected);
    dt.select.currentId(currentId);
    dt.select.init();
  };

  function enableRowCheckboxSelection(dt) {
    const $body = $(dt.table().body());
    const ctx = dt.settings()[0];
    const selector = ctx._select.selector;

    $body.on("click", selector, function(e) {
      const $row = $(this).closest("tr");
      eventTrigger(dt, "selection-event.dt", [$row, e]);
    });
  }

  function selectAllRows(dt) {
    const ctx = dt.settings()[0];
    // Try storing to local storage to prevent calling the server each time.
    const postPromise = $.post(ctx._select.allUrl).then(response => {
      if (typeof ctx._select.formatSelectAllResponseFn === "function") {
        // Left the call code format the response.
        return ctx._select.formatSelectAllResponseFn(response);
      } else {
        // Response should be a list of the id's for the rows to select
        // Needs to be formatted into row_[id]
        return response.map(i => `row_${i}`);
      }
    });

    postPromise.then(data => {
      if (data instanceof Map) {
        ctx._select.selected = data;
        dt.draw();
        eventTrigger(dt, "selection-count.dt", data.size);
        return;
      }
      throw Error(
        "Expected to get a map with key = row_[id] and value of what would be in data-info for the row."
      );
    });
  }

  function selectNone(dt) {
    const ctx = dt.settings()[0];
    ctx._select.selected = new Map();
    dt.draw();
    eventTrigger(dt, "selection-count.dt", 0);
  }

  const apiRegister = DataTable.Api.register;
  const apiRegisterPlural = DataTable.Api.registerPlural;

  /**
   * Trigger an event on a DataTable
   *
   * @param {DataTable.Api} api      DataTable to trigger events on
   * @param  {boolean}      selected true if selected, false if deselected
   * @param  {string}       type     Item type acting on
   *     triggering
   * @private
   */
  function eventTrigger(api, type, args) {
    $(api.table().node()).trigger(type, args);
  }

  apiRegister("select.init()", function() {
    return this.iterator("table", function(ctx) {
      init(ctx);
      eventTrigger(new DataTable.Api(ctx), "selection-count.dt", 0);
    });
  });

  apiRegister("select.selector()", function(selector) {
    return this.iterator("table", function(ctx) {
      ctx._select.selector = selector;
      enableRowCheckboxSelection(new DataTable.Api(ctx));
    });
  });

  apiRegister("select.selectAll()", function() {
    return this.iterator("table", function(ctx) {
      selectAllRows(new DataTable.Api(ctx));
    });
  });

  apiRegister("select.selectNone()", function() {
    return this.iterator("table", function(ctx) {
      selectNone(new DataTable.Api(ctx));
    });
  });

  apiRegister("select.selected()", function(selected) {
    return this.iterator("table", function(ctx) {
      if (typeof selected === "undefined") {
        return ctx._select.selected;
      } else if (selected instanceof Map) {
        ctx._select.selected = selected;
      }
    });
  });

  apiRegister("select.currentId()", function(id) {
    return this.iterator("table", function(ctx) {
      if (typeof id === undefined) {
        return ctx._select.currentId;
      }
      ctx._select.currentId = id;
    });
  });

  function init(ctx) {
    const api = new DataTable.Api(ctx);

    /**
     * Row Created Callback
     *
     * This allows us to check to see whether the checkbox needs
     * to be selector or not.
     */
    ctx.aoRowCreatedCallback.push({
      fn(row) {
        const selected = ctx._select.selected;
        const $row = $(row);
        const id = $row.attr("id");
        if (selected.has(id)) {
          $row.find("input[type=checkbox]").prop("checked", true);
        }
      }
    });

    /**
     * Handles event when row is selected/unselected.
     */
    api.on("selection-event.dt", function(event, row, clickEvent) {
      const id = row.attr("id");
      const info = row.data("info");
      const selected = ctx._select.selected;
      const currentId = ctx._select.currentId;

      if (selected.has(id)) {
        selected.delete(id);
        ctx._select.currentId = undefined;
      } else {
        // Check if the is a multiple selection event!
        if (typeof currentId !== "undefined" && clickEvent.shiftKey) {
          // Get all the ids currently available on the table.
          let inside = false;
          row
            .closest("tbody")
            .find("tr")
            .each((index, tr) => {
              const $tr = $(tr);
              const $trId = $tr.attr("id");

              if ($trId === currentId || $trId === id) {
                inside = !inside;
              } else if (inside) {
                $tr.find('input[type="checkbox"]').prop("checked", true);
                selected.set($trId, $tr.data("info"));
              }
            });
        }
        selected.set(id, info);
        ctx._select.currentId = id;
      }
      eventTrigger(api, "selection-count.dt", selected.size);
    });

    /**
     * Handles event when the number of rows selected have been updated.
     * This updates the rows selected counts.
     */
    api.on("selection-count.dt", function(e, size) {
      let text = "";
      if (0 === size) {
        text = api.i18n("select.none", "No samples selected");
      } else if (1 === size) {
        text = api.i18n("select.one", "1 sample selected");
      } else if (1 < size) {
        text = api
          .i18n("select.other", "1 sample selected")
          .replace("{count}", size);
      }
      $(".selected-counts").html(`<div>${text}</div>`);
    });
  }

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * BUTTONS
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
  $.extend(DataTable.ext.buttons, {
    selectAll: {
      text: i18n("buttons.selectAll", "Select All"),
      className: "btn-sm",
      key: {
        altKey: true,
        key: "s"
      },
      action(e, dt, node, config) {
        dt.select.selectAll();
      }
    },
    selectNone: {
      text: i18n("buttons.selectNone", "Select None"),
      className: "btn-sm",
      key: {
        altKey: true,
        key: "d"
      },
      action(e, dt, node, config) {
        dt.select.selectNone();
      }
    }
  });

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Initialisation
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Attach a listener to the document which listens for DataTables initialisation
  // events so we can automatically initialise
  $(document).on("preInit.dt.dtSelect", function(e, ctx) {
    if (e.namespace !== "dt") {
      return;
    }

    DataTable.select.init(new DataTable.Api(ctx));
  });

  return DataTable.select;
});
