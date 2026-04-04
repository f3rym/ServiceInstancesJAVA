// ─── Datacenter ──────────────────────────────────────────────────
// ManyToMany ↔ Instance (instanceIds returned by server)
export interface DatacenterDto {
  id?: number
  name: string
  location: string
  instanceIds?: number[]
}

// ─── Software ────────────────────────────────────────────────────
// ManyToMany ↔ Instance (via softwareIds on InstanceDto)
export interface SoftwareDto {
  id?: number
  name: string
  version: string
}

// ─── Instance ────────────────────────────────────────────────────
// ManyToMany ↔ Datacenter (datacenterIds)
// ManyToMany ↔ Software   (softwareIds)
export interface InstanceDto {
  id?: number
  name: string
  instanceType: string  // e.g. "t3.medium"
  os: string            // e.g. "Ubuntu 22.04"
  price: number         // cost per hour, >= 1
  datacenterIds: number[]
  softwareIds: number[]
}

// ─── InstanceFilterDto ────────────────────────────────────────────
export interface InstanceFilterDto {
  price?: number | ''
  datacenterLocation?: string
  useNative?: boolean
}

// ─── User ────────────────────────────────────────────────────────
// OneToMany → Orders (orderIds READ_ONLY)
export interface UserDto {
  id?: number
  username: string
  orderIds?: number[]
}

// ─── Order ───────────────────────────────────────────────────────
export interface OrderDto {
  id?: number
  userId: number
  instanceId: number
}

// ─── Pagination ───────────────────────────────────────────────────
export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}
