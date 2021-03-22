/**
 * @fileOverview Utility class to help with common ajax request and response functions
 */

/**
 * Add a key to an object for React
 * @param {Object} item - the object that needs a key
 * @param {string} label - the type of object, this will be the unique preface for the key
 * @param {string} [attr=id] - the attribute on the object to pull the unique identifier (usually the id)
 * @returns {*&{key: string}} - copy of initial object with the key added
 */
const addKeyToItem = (item, label, attr = "id") => ({
  ...item,
  key: `${label}-${item[attr]}`,
});

/**
 * Add keys to all Objects in a list for React
 * @param {Object[]} list - The items to add a key to.
 * @param {string} label - the type of object, this will be the unique preface for the key
 * @param {string} [attr=id] - the attribute on the object to pull the unique identifier (usually the id)
 * @returns {*} - copy of list with the keys added to each entry
 */
export const addKeysToList = (list, label, attr = "id") => {
  if (!Array.isArray(list)) {
    throw new Error(`"addKeysToList" requires a list`);
  } else if (!list.length) {
    return list;
  } else if (typeof list[0][attr] === "undefined") {
    throw new Error(
      `Objects in the list passed to "addKeysToList" do not have the attribute ${attr}`
    );
  }
  return list.map((item) => addKeyToItem(item, label, attr));
};
