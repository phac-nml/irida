/**
 * This file is strongly based off the DataTables original select plugin,
 * but works with server side paging and OS checkbox.
 */
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
    /**
     * Assign opts to the context so we can easily access it later.
     */
    ctx._select = Object.assign({}, opts);

    // Default items
    /**
     * Default selector for the row.
     */
    const selector = "input[type=checkbox]";
    /**
     * All currently selected row are stored in a Map with the key
     * being the row id (row_[id]) and the value equal to the value
     * in the rows data-info attribute.
     */
    const selected = new Map();
    /**
     * The id for the most recently selected row.  This is stored for multi-row
     * selection.
     */
    let currentId = undefined;

    // Initialize selection
    dt.select.selector(selector);
    dt.select.selected(selected);
    dt.select.currentId(currentId);
    dt.select.init();
  };

  /**
   * Set up click handlers for the checkbox.
   * @param dt {object} current DataTable
   */
  function enableRowCheckboxSelection(dt) {
    const $body = $(dt.table().body());
    const ctx = dt.settings()[0];
    const selector = ctx._select.selector;

    $body.on("click", selector, function(e) {
      const $row = $(this).closest("tr");
      eventTrigger(dt, "selection-event.dt", [$row, e]);
    });
  }

  /**
   * Select all rows in the table.
   * This requires a server call, and a help function should be supplied
   * in the select config (formatSelectAllResponseFn)
   * @param dt {object} Current DataTable
   */
  function selectAllRows(dt) {
    const ctx = dt.settings()[0];
    /*
    Need to get the filters so we only select the available samples.
     */
    const data = dt.ajax.params();

    // Try storing to local storage to prevent calling the server each time.
    const postPromise = $.post(
      ctx._select.allUrl,
      data,
      ctx._select.allPostDataFn()
    ).then(response => {
      if (typeof ctx._select.formatSelectAllResponseFn === "function") {
        // Let the user handle formatting response.
        return ctx._select.formatSelectAllResponseFn(response);
      }
      throw new Error("Expected supplied function [formatSelectAllResponseFn]");
    });

    postPromise.then(data => {
      if (data instanceof Map) {
        ctx._select.selected = data;
        dt.draw();
        eventTrigger(dt, "selection-count.dt", data.size);
        return;
      }
      throw new Error(
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

  /**
   * Trigger an event on a DataTable
   *
   * @param api {object} DataTable api to trigger events on
   * @param type {boolean} the type of event that was triggered
   * @param args {string|object} arguments to pass to the handler
   * @private
   */
  function eventTrigger(api, type, args) {
    $(api.table().node()).trigger(type, args);
  }

  /**
   * Register the select initialization function with Datatables
   */
  apiRegister("select.init()", function() {
    return this.iterator("table", function(ctx) {
      init(ctx);
      eventTrigger(new DataTable.Api(ctx), "selection-count.dt", 0);
    });
  });

  /**
   * Register the table selector with DataTables
   */
  apiRegister("select.selector()", function(selector) {
    return this.iterator("table", function(ctx) {
      ctx._select.selector = selector;
      enableRowCheckboxSelection(new DataTable.Api(ctx));
    });
  });

  /**
   * Register the select table function with DataTables
   */
  apiRegister("select.selectAll()", function() {
    return this.iterator("table", function(ctx) {
      selectAllRows(new DataTable.Api(ctx));
    });
  });

  /**
   * Register the select none function with DataTables
   */
  apiRegister("select.selectNone()", function() {
    return this.iterator("table", function(ctx) {
      selectNone(new DataTable.Api(ctx));
    });
  });

  /**
   * Register the selected rows map with DataTables
   */
  apiRegister("select.selected()", function(selected) {
    return this.iterator("table", function(ctx) {
      if (typeof selected === "undefined") {
        return ctx._select.selected;
      } else if (selected instanceof Map) {
        ctx._select.selected = selected;
      }
    });
  });

  /**
   * Register the ability to get and set the currentId with DataTables
   */
  apiRegister("select.currentId()", function(id) {
    return this.iterator("table", function(ctx) {
      if (typeof id === "undefined") {
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
                /*
                 * Does not matter if the user selects above or below the previous
                 * one.  As soon as this hits a row that is either the previous current
                 * or the one just click this will become true.  Once it hits the respective
                 * opposite row this wil be false.  In between, everything will be selected.
                 */
                inside = !inside;
              } else if (inside) {
                // Capture the row id and the data-info for the row.
                selected.set($trId, $tr.data("info"));
                // Show the user that the row is selected.
                $tr.find('input[type="checkbox"]').prop("checked", true);
              }
            });
        }
        selected.set(id, info);
        ctx._select.currentId = id;
      }
      /**
       * Fire the event for the number of rows that have been selected.
       */
      eventTrigger(api, "selection-count.dt", selected.size);
    });

    /**
     * Handles event when the number of rows selected have been updated.
     * This updates the rows selected counts.
     */
    api.on("selection-count.dt", function(e, size) {
      let text = "";
      if (0 === size) {
        text = api.i18n("select.none", "No rows selected");
      } else if (1 === size) {
        text = api.i18n("select.one", "1 row selected");
      } else if (1 < size) {
        text = api
          .i18n("select.other", `${size} rows selected`)
          .replace("{count}", size);
      }
      $(".selected-counts").html(`<div>${text}</div>`);
    });
  }

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * BUTTONS
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
  Object.assign(DataTable.ext.buttons, {
    selectAll: {
      text: i18n("buttons.selectAll", "Select All"),
      className: "btn-sm dt-select-all",
      key: {
        altKey: true,
        key: "s"
      },
      action(e, dt) {
        dt.select.selectAll();
      }
    },
    selectNone: {
      text: i18n("buttons.selectNone", "Select None"),
      className: "btn-sm dt-select-none",
      key: {
        altKey: true,
        key: "d"
      },
      action(e, dt) {
        dt.select.selectNone();
      }
    }
  });

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * Initialization
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Attach a listener to the document which listens for DataTables initialization
  // events so we can automatically initialize
  $(document).on("preInit.dt.dtSelect", function(e, ctx) {
    if (e.namespace !== "dt") {
      return;
    }

    /*
     * Initialize select within the DataTable.
     */
    DataTable.select.init(new DataTable.Api(ctx));
  });

  return DataTable.select;
});
