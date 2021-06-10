export default function buildSearchQueryOpts(searchQuery) {
  let search = '';
  if (searchQuery && searchQuery.search) {
    search = `&search=${searchQuery.search}`;
  }
  return search;
}
