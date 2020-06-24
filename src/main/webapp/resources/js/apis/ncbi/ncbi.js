export async function getNCBIExports({ url }) {
  const response = await fetch(url);
  return response.json();
}
