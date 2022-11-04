import { fetchCurrentUserDetails } from "../../../apis/users/user";
export default async function loader() {
  return fetchCurrentUserDetails();
}
