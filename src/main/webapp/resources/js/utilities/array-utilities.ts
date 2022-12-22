/**
 * Splits an array into multiple, smaller arrays
 *
 * @param items the array to be chunked
 *
 * @returns a chunked array
 */
export function chunkArray(items: any[]) {
  const size = calculateChunkSize(items.length);
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
function calculateChunkSize(arraySize: number) {
  const MIN = 10;
  const MAX = 100;
  const estimated = Math.ceil(arraySize / 10);
  return estimated < MIN ? MIN : estimated > MAX ? MAX : estimated;
}
