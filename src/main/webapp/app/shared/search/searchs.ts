export default function buildSearchQueryOpts(searchQuery) {
  let query = '';
  if (searchQuery && searchQuery.search) {
    query = `&query=${searchQuery.search}`;
  }
  return query;
}
