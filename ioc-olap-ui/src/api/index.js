import * as login from './login'
import * as menu from './menu'
import * as user from './user'
import * as role from './role'
import * as common from './common'
export default {
  ...login,
  ...menu,
  ...user,
  ...role,
  ...common
}
