import $ from "jquery";
import "./../../vendor/datatables/datatables";

$("#filesTable").DataTable();

$("button.download-file").on("click", function() {
  const that = $(this);
  const fileId = that.data("file-id");
  const objectId = that.data("object-id");
  const iframe = document.createElement("iframe");
  iframe.src = `${window.TL.BASE_URL}sequenceFiles/download/${objectId}/file/${fileId}`;
  iframe.style.display = "none";
  document.body.appendChild(iframe);
});
