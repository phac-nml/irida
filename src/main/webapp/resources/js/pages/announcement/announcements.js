import $ from "jquery";
import showdown from "showdown";
import { fromNow } from "../../utilities/date-utilities";
import "../../../css/modules/announcements.css";

const markdownConverter = new showdown.Converter();

// Change all the markdown in each announcment to HTML use showdown.
$(".announcement-item").each((i, elm) => {
  const $elm = $(elm);
  const message = $elm.data("message");
  const createdDate = $elm.data("createddate");
  const readon = $elm.data("readon");
  $elm.find(".markdown").html(markdownConverter.makeHtml(message));
  $elm.find(".created-date").html(fromNow({ date: createdDate }));
  $elm.find(".readon").html(fromNow({ date: readon }));
  $elm.css("display", "block");
});
