/**
 * Create awaitable XHR request
 *
 * @param {string} url URL
 * @param {string} method HTTP method
 * @param {string} json JSON string
 * @param {number} timeout Timeout in milliseconds
 * @return {Promise<string>} Awaitable Promise of XHR request
 */
export function ajaxRequest({
  url,
  method = "get",
  json = null,
  timeout = 60000
}) {
  return new Promise(function(resolve, reject) {
    const xhr = new XMLHttpRequest();
    xhr.timeout = timeout;
    xhr.onreadystatechange = function() {
      if (xhr.readyState === 4) {
        if (xhr.status >= 200 && xhr.status < 300) {
          resolve(xhr.response);
        } else {
          reject(xhr);
        }
      }
    };

    xhr.ontimeout = function() {
      reject("timeout");
    };
    xhr.open(method, url, true);
    if (json) {
      xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
      xhr.send(json);
    } else {
      xhr.send();
    }
  });
}
