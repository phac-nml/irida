/**
 * Given some malformed JSON string, return a list of tokens missing their pairs.
 *
 * Each {, [, " needs their closing ", ], }
 *
 * Add dummy key-value if necessary.
 *
 * Examples:
 *
 * > missingTokensStack('{"hell') => ["{", ":"..."", """]
 * > missingTokensStack('[{"') => ["[", "{", "key":"...""]
 *
 * @param {string} malformedJSON Malformed JSON string
 * @returns {Array} JSON tokens missing pairs.
 */
function missingTokensStack(malformedJSON) {
  // stack to keep track of JSON significant tokens
  const stack = [];
  // Generator to look through string character by character
  function* gen(x) {
    yield* x;
  }
  const genJson = gen(malformedJSON);
  /**
   * Last non-whitespace
   * @type {string}
   */
  let last = null;
  /**
   * Current character in iteration.
   * @type {string}
   */
  let curr = null;
  /**
   * Are we in the middle of a map key?
   * @type {boolean}
   */
  let isInKey = false;
  while (true) {
    const next = genJson.next();
    curr = next.value;
    const lastItem = stack[stack.length - 1];
    /**
     * Are we in the middle of a string?
     * @type {boolean}
     */
    const isInQuote = lastItem === '"';
    switch (curr) {
      case "[":
        if (isInQuote) break;
        stack.push(curr);
        break;
      case "{":
        if (isInQuote) break;
        stack.push(curr);
        break;
      case "]":
        if (isInQuote) break;
        stack.pop();
        break;
      case "}":
        if (isInQuote) break;
        stack.pop();
        break;
      case '"':
        // escaped string?
        if (last === "\\") break;
        // closing quote?
        if (isInQuote) {
          stack.pop();
          break;
        }
        // if the previous non-whitespace character was a `,` or `{`, then we
        // are in a map key
        if ((last === "," || last === "{") && lastItem === "{") {
          isInKey = true;
        }
        stack.push(curr);
        break;
      case ":":
        if (isInQuote) break;
        isInKey = false;
        break;
      default:
        break;
    }
    // keep track of last non-whitespace character
    if (/\S/.test(curr)) {
      last = curr;
    }
    if (next.done) break;
  }
  // depending on what the last character is in the malformed JSON string and
  // the last token on the stack or if we're in a map key at the end of the
  // string, then add a dummy key
  const lastChar = malformedJSON[malformedJSON.length - 1];
  const lastStackItem = stack[stack.length - 1];
  if (isInKey && lastChar !== '"') {
    // if at the end of the JSON string, we're in a map key and the last
    // character isn't a quote `"`, then insert a dummy value for the truncated
    // key
    const tmp = stack.pop();
    stack.push(':"..."');
    stack.push(tmp);
  } else if (isInKey && lastChar === '"') {
    // if the last character is a quote and we're in a map key, insert a dummy
    // key-value pair
    stack.pop();
    // notice no beginning quote for "key\"...", the beginning quote is
    // accounted for by the `lastChar` being "
    stack.push('key":"..."');
  } else if (lastChar === "," && lastStackItem === "{") {
    // if we're in a map and the last character is a comma, insert a full dummy
    // key-value pair
    stack.push('"key":"..."');
  } else if (lastChar === ":" && !isInKey && lastStackItem === "{") {
    stack.push("0");
  }
  return stack;
}

function appendMissingTokens(malformedJson, missingTokens) {
  const tokenSubMap = {
    "{": "}",
    "[": "]",
    '"': '"'
  };
  return (
    malformedJson +
    missingTokens
      .map(x => {
        if (tokenSubMap.hasOwnProperty(x)) {
          return tokenSubMap[x];
        }
        return x;
      })
      .reverse()
      .join("")
  );
}

export function repairMalformedJSON(malformedJSON) {
  const missingTokens = missingTokensStack(malformedJSON);
  return JSON.parse(appendMissingTokens(malformedJSON, missingTokens));
}
