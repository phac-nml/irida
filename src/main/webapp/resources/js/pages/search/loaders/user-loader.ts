import { CurrentUser, fetchCurrentUserDetails } from "../../../apis/users/user";
export default async function loader(): Promise<CurrentUser> {
  return await fetchCurrentUserDetails();
}
