/**
 * Splits an array into multiple, smaller arrays of the given size
 *
 * @param items the array to be chunked
 * @param size the size of the chunk
 *
 * @returns a chunked array
 */
export function chunkArray(items: any[], size: number) {
  const chunks = [];
  items = [].concat(...items);

  while (items.length) {
    chunks.push(items.splice(0, size));
  }

  return chunks;
}

/**
 * Calculate the best request chunk size
 *
 * @param arraySize the size of the array
 *
 * @returns the chunk size
 */
export function calculateChunkSize(arraySize: number) {
  const MIN = 10;
  const MAX = 100;
  const estimated = Math.ceil(arraySize / 10);
  return estimated < MIN ? MIN : estimated > MAX ? MAX : estimated;
}
