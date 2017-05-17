function downloadItem({format = 'xlsx'}) {
  const url = `${window.PAGE.urls.export}&dtf=${format}`;
  const anchor = document.createElement('a');
  anchor.style.display='none';
  anchor.href = url;
  anchor.click();
}

if (typeof window.PAGE === 'object') {
  const table = $('#projects').DataTable({
    dom: `<".row"<".col-md-8.buttons"B><".col-md-4"f>>rt<".row"<".col-md-3"l><".col-md-6"p><".col-md-3"i>>`,
    buttons: [
      {
        extend: 'collection',
        text: '<i class="fa fa-download" aria-hidden="true"></i> Export&nbsp;&nbsp;<i class="fa fa-caret-down" aria-hidden="true"></i>',
        buttons: window.buttons.map(button => ({
          text: button.name,
          action() {
            downloadItem({format: button.format});
          }
        }))
      }
    ],
    processing: true,
    serverSide: true,
    ajax: window.PAGE.urls.projects,
    order: [[5, "desc"]],
    columnDefs: [
      {
        targets: [1],
        render: function(data, type, full) {
          return `<a class="btn btn-link" href="${full.id}">${data}</a>`;
        },
      },
      {
        targets: [4, 5],
        render: function(data) {
          return `<span data-toggle="tooltip" data-placement="top" title="${moment(data)}" data-livestamp="${moment(data).unix()}"><i class="fa fa-spinner fa-pulse fa-fw"></i></span>`
        },
      },
    ],
    createdRow: function(row) {
      $(row).tooltip({selector: '[data-toggle="tooltip"]'});
    },
  });
}