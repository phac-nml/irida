import { fetchCurrentUserDetails } from "../../../apis/users/user";
export async function loader() {
  return fetchCurrentUserDetails();
}
