import $ from "jquery";
import { setBaseUrl } from "../../utilities/url-utilities";

$("button.download-file").on("click", function() {
  const that = $(this);
  const fileId = that.data("file-id");
  const objectId = that.data("object-id");
  const iframe = document.createElement("iframe");
  iframe.src = setBaseUrl(`sequenceFiles/download/${objectId}/file/${fileId}`);
  iframe.style.display = "none";
  document.body.appendChild(iframe);
});
