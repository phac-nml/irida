const angular = require('angular');
import uiRouter from 'angular-ui-router';
import {states} from "./sample-metadata/router/config";
import dropzone from "./directives/dropzone";
import {sampleMetadataService} from "./factories/metadataImportService";

const app = angular.module('irida');
app.requires.push(uiRouter);
app
  .config(states)
  .directive("dropzone", dropzone)
  .service("sampleMetadataService", sampleMetadataService);

// /* eslint new-cap: ["error", { "properties": false }] */
// var $ = require('jquery');
// import Dropzone from 'dropzone';
//
// /**
//  * Create a table from the metadata return after parsing the excel docs.
//  * @param {array} data from the datatable
//  */
// const tableCreator = data => {
//   const nameCol = data.headers.indexOf('NLEP #');
//   const headers = transformHeaders(data.headers);
//
//   /**
//    * Create an object of the headers title and text.
//    * @param {array} headers list of table headers
//    * @return {array} of formatted data for table headers.
//    */
//   function transformHeaders(headers) {
//     return headers.map(function(h) {
//       return {title: h, data: h};
//     });
//   }
//
//   $('#metadata-table').DataTable({
//     scrollX: true,
//     data: data.metadata,
//     columns: headers,
//     createdRow: function(row, data) {
//       if (data.sampleId === '') {
//         $(row).addClass('bg-danger');
//       }
//     },
//     columnDefs: [
//       {
//         targets: nameCol,
//         render: function(data, type, row) {
//           var id = row.sampleId;
//           if (id === '') {
//             return data;
//           }
//           return `<a href="/samples/${id}">${data}</a>`;
//         }
//       }
//     ]
//   });
// };
//
// // Configuration for dropzone.js allowing for user to
// // upload their excel files.
// Dropzone.options.metadataDropzone = {
//   paramName: 'file',
//   acceptedFiles: '.xlx,.xlsx',
//   init: function() {
//     var data;
//     this.on('success', function(file, result) {
//       data = result;
//     });
//     this.on('complete', function() {
//       $('#metadataDropzone').fadeOut(200, function() {
//         $(this).remove();
//         tableCreator(data);
//       });
//     });
//   }
// };
